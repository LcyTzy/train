package com.zhenyu.train.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhenyu.train.common.constant.CommonConstant;
import com.zhenyu.train.common.enums.OrderStatusEnum;
import com.zhenyu.train.common.exception.BusinessException;
import com.zhenyu.train.common.result.ResultCode;
import com.zhenyu.train.common.util.UserContext;
import com.zhenyu.train.order.dto.*;
import com.zhenyu.train.order.entity.Order;
import com.zhenyu.train.order.entity.OrderDetail;
import com.zhenyu.train.order.mapper.OrderDetailMapper;
import com.zhenyu.train.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final RocketMQTemplate rocketMQTemplate;
    private final com.zhenyu.train.common.feign.TicketServiceClient ticketServiceClient;

    /**
     * 提交购票请求(异步)
     */
    public TicketSubmitResponse submitTicket(TicketSubmitRequest request) {
        String requestNo = UUID.randomUUID().toString().replace("-", "");

        // 发送消息到RocketMQ
        TicketSubmitMessage message = new TicketSubmitMessage();
        message.setRequestNo(requestNo);
        message.setUserId(UserContext.getUserId());
        message.setTrainId(request.getTrainId());
        message.setTrainDate(request.getTrainDate());
        message.setStartStationId(request.getStartStationId());
        message.setEndStationId(request.getEndStationId());
        message.setSeatType(request.getSeatType());
        message.setPassengerIds(request.getPassengerIds());

        rocketMQTemplate.convertAndSend("ticket-submit-topic", message);

        TicketSubmitResponse response = new TicketSubmitResponse();
        response.setRequestNo(requestNo);
        return response;
    }

    /**
     * 查询购票结果
     */
    public TicketResultResponse getTicketResult(String requestNo) {
        // 从数据库查询订单（通过requestNo关联）
        // 这里简化处理，实际应该通过消息ID或其他方式关联
        TicketResultResponse response = new TicketResultResponse();
        
        // 查询最近的订单
        Long userId = UserContext.getUserId();
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .orderByDesc(Order::getCreateTime)
                        .last("LIMIT 1"));
        
        if (order == null) {
            response.setStatus(0);
            response.setMessage("处理中");
            return response;
        }
        
        // 根据订单状态返回结果
        if (order.getOrderStatus() == OrderStatusEnum.PENDING_PAYMENT.getCode()) {
            response.setStatus(1);
            response.setMessage("购票成功，待支付");
            response.setOrderNo(order.getOrderNo());
            response.setTotalAmount(order.getTotalAmount());
        } else if (order.getOrderStatus() == OrderStatusEnum.TICKET_ISSUED.getCode()) {
            response.setStatus(2);
            response.setMessage("出票成功");
            response.setOrderNo(order.getOrderNo());
            response.setTotalAmount(order.getTotalAmount());
        } else if (order.getOrderStatus() == OrderStatusEnum.CANCELLED.getCode() || 
                   order.getOrderStatus() == OrderStatusEnum.PAYMENT_TIMEOUT.getCode()) {
            response.setStatus(3);
            response.setMessage("购票失败");
        } else {
            response.setStatus(0);
            response.setMessage("处理中");
        }
        
        return response;
    }

    /**
     * 创建订单
     */
    @Transactional
    public Order createOrder(OrderCreateRequest request) {
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(request.getUserId());
        order.setTrainId(request.getTrainId());
        order.setTrainNo(request.getTrainNo());
        order.setTrainDate(request.getTrainDate());
        order.setStartStationId(request.getStartStationId());
        order.setStartStationName(request.getStartStationName());
        order.setEndStationId(request.getEndStationId());
        order.setEndStationName(request.getEndStationName());
        order.setDepartTime(request.getDepartTime());
        order.setArriveTime(request.getArriveTime());
        order.setTotalAmount(request.getTotalAmount());
        order.setPassengerCount(request.getPassengerCount());
        order.setOrderStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());
        order.setExpireTime(LocalDateTime.now().plusMinutes(CommonConstant.ORDER_PAY_TIMEOUT_MINUTES));

        orderMapper.insert(order);

        // 创建订单明细
        for (OrderDetailCreateRequest detailReq : request.getDetails()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(order.getId());
            detail.setPassengerId(detailReq.getPassengerId());
            detail.setPassengerName(detailReq.getPassengerName());
            detail.setPassengerIdType(detailReq.getPassengerIdType());
            detail.setPassengerIdNumber(detailReq.getPassengerIdNumber());
            detail.setCarriageNo(detailReq.getCarriageNo());
            detail.setSeatNo(detailReq.getSeatNo());
            detail.setSeatType(detailReq.getSeatType());
            detail.setPrice(detailReq.getPrice());
            detail.setTicketStatus(0);
            orderDetailMapper.insert(detail);
        }

        return order;
    }

    /**
     * 获取订单列表
     */
    public Page<Order> getOrderList(Integer status, Integer page, Integer size) {
        Long userId = UserContext.getUserId();
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null) {
            wrapper.eq(Order::getOrderStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return orderMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 获取订单详情
     */
    public Order getOrderDetail(String orderNo) {
        Long userId = UserContext.getUserId();
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo)
                        .eq(Order::getUserId, userId));
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        return order;
    }

    /**
     * 获取订单明细
     */
    public List<OrderDetail> getOrderDetails(Long orderId) {
        return orderDetailMapper.selectList(
                new LambdaQueryWrapper<OrderDetail>()
                        .eq(OrderDetail::getOrderId, orderId));
    }

    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(String orderNo) {
        Long userId = UserContext.getUserId();
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo)
                        .eq(Order::getUserId, userId));

        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }

        if (order.getOrderStatus() != OrderStatusEnum.PENDING_PAYMENT.getCode()) {
            throw new BusinessException(ResultCode.ORDER_CANNOT_CANCEL, "订单状态不允许取消");
        }

        order.setOrderStatus(OrderStatusEnum.CANCELLED.getCode());
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 释放座位库存
        List<OrderDetail> details = getOrderDetails(order.getId());
        for (OrderDetail detail : details) {
            try {
                ticketServiceClient.restoreStock(
                        order.getTrainId(),
                        order.getTrainDate().toString(),
                        detail.getCarriageNo(),
                        detail.getSeatType(),
                        1
                );
                log.info("库存释放成功: orderNo={}, carriageNo={}, seatType={}", 
                        orderNo, detail.getCarriageNo(), detail.getSeatType());
            } catch (Exception e) {
                log.error("库存释放失败: orderNo={}, carriageNo={}, seatType={}", 
                        orderNo, detail.getCarriageNo(), detail.getSeatType(), e);
            }
        }
    }

    /**
     * 退票
     */
    @Transactional
    public void refundOrder(String orderNo) {
        Long userId = UserContext.getUserId();
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo)
                        .eq(Order::getUserId, userId));

        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }

        if (order.getOrderStatus() != OrderStatusEnum.TICKET_ISSUED.getCode()) {
            throw new BusinessException(ResultCode.ORDER_CANNOT_REFUND, "订单状态不允许退票");
        }

        order.setOrderStatus(OrderStatusEnum.REFUNDED.getCode());
        orderMapper.updateById(order);

        // 更新订单明细状态
        List<OrderDetail> details = getOrderDetails(order.getId());
        for (OrderDetail detail : details) {
            detail.setTicketStatus(1); // 已退票
            detail.setRefundTime(LocalDateTime.now());
            orderDetailMapper.updateById(detail);
            
            // 恢复库存
            try {
                ticketServiceClient.restoreStock(
                        order.getTrainId(),
                        order.getTrainDate().toString(),
                        detail.getCarriageNo(),
                        detail.getSeatType(),
                        1
                );
                log.info("退票库存释放成功: orderNo={}, carriageNo={}, seatType={}", 
                        orderNo, detail.getCarriageNo(), detail.getSeatType());
            } catch (Exception e) {
                log.error("退票库存释放失败: orderNo={}, carriageNo={}, seatType={}", 
                        orderNo, detail.getCarriageNo(), detail.getSeatType(), e);
            }
        }

        // TODO: 发起退款（调用支付服务）
    }

    /**
     * 支付成功回调
     */
    @Transactional
    public void paySuccess(String orderNo) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo));

        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }

        if (order.getOrderStatus() != OrderStatusEnum.PENDING_PAYMENT.getCode()) {
            throw new BusinessException(ResultCode.ORDER_ALREADY_PAID);
        }

        order.setOrderStatus(OrderStatusEnum.TICKET_ISSUED.getCode());
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    /**
     * 定时任务：处理超时订单
     */
    // @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void handleTimeoutOrders() {
        List<Order> timeoutOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderStatus, OrderStatusEnum.PENDING_PAYMENT.getCode())
                        .lt(Order::getExpireTime, LocalDateTime.now()));

        for (Order order : timeoutOrders) {
            order.setOrderStatus(OrderStatusEnum.PAYMENT_TIMEOUT.getCode());
            order.setCancelTime(LocalDateTime.now());
            orderMapper.updateById(order);
            log.info("订单超时取消: {}", order.getOrderNo());
            
            // 释放座位库存
            List<OrderDetail> details = getOrderDetails(order.getId());
            for (OrderDetail detail : details) {
                try {
                    ticketServiceClient.restoreStock(
                            order.getTrainId(),
                            order.getTrainDate().toString(),
                            detail.getCarriageNo(),
                            detail.getSeatType(),
                            1
                    );
                    log.info("超时订单库存释放成功: orderNo={}, carriageNo={}, seatType={}", 
                            order.getOrderNo(), detail.getCarriageNo(), detail.getSeatType());
                } catch (Exception e) {
                    log.error("超时订单库存释放失败: orderNo={}, carriageNo={}, seatType={}", 
                            order.getOrderNo(), detail.getCarriageNo(), detail.getSeatType(), e);
                }
            }
        }
    }

    private String generateOrderNo() {
        return "T" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
