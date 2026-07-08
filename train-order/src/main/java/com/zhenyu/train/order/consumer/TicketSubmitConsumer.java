package com.zhenyu.train.order.consumer;

import com.zhenyu.train.common.feign.TrainServiceClient;
import com.zhenyu.train.common.feign.UserServiceClient;
import com.zhenyu.train.common.feign.TicketServiceClient;
import com.zhenyu.train.order.dto.OrderCreateRequest;
import com.zhenyu.train.order.dto.OrderDetailCreateRequest;
import com.zhenyu.train.order.dto.TicketSubmitMessage;
import com.zhenyu.train.order.entity.Order;
import com.zhenyu.train.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 购票消息消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(topic = "ticket-submit-topic", consumerGroup = "ticket-submit-consumer-group")
public class TicketSubmitConsumer implements RocketMQListener<TicketSubmitMessage> {

    private final OrderService orderService;
    private final TrainServiceClient trainServiceClient;
    private final UserServiceClient userServiceClient;
    private final TicketServiceClient ticketServiceClient;

    @Override
    public void onMessage(TicketSubmitMessage message) {
        log.info("收到购票消息: requestNo={}", message.getRequestNo());

        try {
            // 1. 调用车次服务获取车次信息
            Map<String, Object> trainInfo = trainServiceClient.getTrainInfo(message.getTrainId());
            if (trainInfo.isEmpty()) {
                log.error("车次不存在: trainId={}", message.getTrainId());
                return;
            }

            // 2. 调用车次服务获取站点信息
            Map<String, Object> startStationInfo = trainServiceClient.getStationInfo(message.getStartStationId());
            Map<String, Object> endStationInfo = trainServiceClient.getStationInfo(message.getEndStationId());
            if (startStationInfo.isEmpty() || endStationInfo.isEmpty()) {
                log.error("站点不存在: startId={}, endId={}", message.getStartStationId(), message.getEndStationId());
                return;
            }

            // 3. 调用车次服务获取车次站点信息（获取出发到达时间）
            Map<String, Object> startTrainStation = trainServiceClient.getTrainStationInfo(message.getTrainId(), message.getStartStationId());
            Map<String, Object> endTrainStation = trainServiceClient.getTrainStationInfo(message.getTrainId(), message.getEndStationId());

            // 4. 扣减库存（简化处理，假设车厢号为1）
            boolean deducted = ticketServiceClient.deductStock(
                    message.getTrainId(),
                    message.getTrainDate().toString(),
                    1, // 车厢号
                    message.getSeatType(),
                    message.getPassengerIds().size()
            );

            if (!deducted) {
                log.error("库存扣减失败: trainId={}", message.getTrainId());
                return;
            }

            // 5. 查询乘客信息并创建订单明细
            List<OrderDetailCreateRequest> details = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (Long passengerId : message.getPassengerIds()) {
                Map<String, Object> passengerInfo = userServiceClient.getPassengerInfo(passengerId);
                if (passengerInfo.isEmpty()) {
                    log.error("乘客不存在: passengerId={}", passengerId);
                    continue;
                }

                OrderDetailCreateRequest detail = new OrderDetailCreateRequest();
                detail.setPassengerId(passengerId);
                detail.setPassengerName((String) passengerInfo.get("name"));
                detail.setPassengerIdType((Integer) passengerInfo.get("idType"));
                detail.setPassengerIdNumber((String) passengerInfo.get("idNumber"));
                detail.setCarriageNo(1);
                detail.setSeatNo("01A"); // 简化处理
                detail.setSeatType(message.getSeatType());
                detail.setPrice(new BigDecimal("553.00")); // 简化处理，应该查询票价
                details.add(detail);

                totalAmount = totalAmount.add(detail.getPrice());
            }

            // 6. 创建订单
            OrderCreateRequest orderRequest = new OrderCreateRequest();
            orderRequest.setUserId(message.getUserId());
            orderRequest.setTrainId(message.getTrainId());
            orderRequest.setTrainNo((String) trainInfo.get("trainNo"));
            orderRequest.setTrainDate(message.getTrainDate());
            orderRequest.setStartStationId(message.getStartStationId());
            orderRequest.setStartStationName((String) startStationInfo.get("stationName"));
            orderRequest.setEndStationId(message.getEndStationId());
            orderRequest.setEndStationName((String) endStationInfo.get("stationName"));
            orderRequest.setDepartTime(LocalTime.parse(startTrainStation.get("departTime").toString()));
            orderRequest.setArriveTime(LocalTime.parse(endTrainStation.get("arriveTime").toString()));
            orderRequest.setTotalAmount(totalAmount);
            orderRequest.setPassengerCount(details.size());
            orderRequest.setDetails(details);

            Order order = orderService.createOrder(orderRequest);
            log.info("订单创建成功: orderNo={}", order.getOrderNo());

        } catch (Exception e) {
            log.error("购票消息处理失败: requestNo={}", message.getRequestNo(), e);
        }
    }
}
