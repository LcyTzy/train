package com.zhenyu.train.pay.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhenyu.train.common.exception.BusinessException;
import com.zhenyu.train.common.feign.OrderServiceClient;
import com.zhenyu.train.common.result.ResultCode;
import com.zhenyu.train.common.util.UserContext;
import com.zhenyu.train.pay.dto.PayCallbackRequest;
import com.zhenyu.train.pay.dto.PayCreateRequest;
import com.zhenyu.train.pay.dto.PayCreateResponse;
import com.zhenyu.train.pay.entity.Pay;
import com.zhenyu.train.pay.mapper.PayMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 支付服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayService {

    private final PayMapper payMapper;
    private final OrderServiceClient orderServiceClient;

    /**
     * 创建支付
     */
    @Transactional
    public PayCreateResponse createPay(PayCreateRequest request) {
        Long userId = UserContext.getUserId();

        // 调用订单服务获取订单信息
        java.util.Map<String, Object> orderInfo = orderServiceClient.getOrderInfo(request.getOrderNo());
        if (orderInfo == null || orderInfo.isEmpty()) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }

        // 验证订单状态
        Integer orderStatus = (Integer) orderInfo.get("orderStatus");
        if (orderStatus == null || orderStatus != 0) { // 0-待支付
            throw new BusinessException(ResultCode.ORDER_ALREADY_PAID, "订单状态不允许支付");
        }

        // 验证订单用户
        Long orderUserId = orderInfo.get("userId") != null ? Long.valueOf(orderInfo.get("userId").toString()) : null;
        if (orderUserId == null || !orderUserId.equals(userId)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "无权支付该订单");
        }

        BigDecimal amount = new BigDecimal(orderInfo.get("totalAmount").toString());
        Long orderId = Long.valueOf(orderInfo.get("orderId").toString());

        // 检查是否已存在支付记录
        Pay existPay = payMapper.selectOne(
                new LambdaQueryWrapper<Pay>()
                        .eq(Pay::getOrderNo, request.getOrderNo())
                        .eq(Pay::getPayStatus, 0)); // 待支付

        if (existPay != null) {
            // 返回已存在的支付信息
            PayCreateResponse response = new PayCreateResponse();
            response.setPayNo(existPay.getPayNo());
            response.setPayUrl(generatePayUrl(existPay.getPayNo()));
            response.setAmount(existPay.getAmount());
            return response;
        }

        // 创建支付记录
        Pay pay = new Pay();
        pay.setPayNo(generatePayNo());
        pay.setOrderId(orderId);
        pay.setOrderNo(request.getOrderNo());
        pay.setUserId(userId);
        pay.setAmount(amount);
        pay.setPayChannel(request.getPayChannel());
        pay.setPayStatus(0); // 待支付

        payMapper.insert(pay);

        PayCreateResponse response = new PayCreateResponse();
        response.setPayNo(pay.getPayNo());
        response.setPayUrl(generatePayUrl(pay.getPayNo()));
        response.setAmount(amount);

        return response;
    }

    /**
     * 支付回调
     */
    @Transactional
    public void payCallback(PayCallbackRequest request) {
        log.info("收到支付回调: payNo={}, status={}", request.getPayNo(), request.getStatus());

        Pay pay = payMapper.selectOne(
                new LambdaQueryWrapper<Pay>()
                        .eq(Pay::getPayNo, request.getPayNo()));

        if (pay == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "支付记录不存在");
        }

        if (pay.getPayStatus() != 0) {
            log.warn("支付记录已处理: payNo={}", request.getPayNo());
            return;
        }

        // 验证签名
        if (!verifySign(request)) {
            throw new BusinessException(ResultCode.PAY_CALLBACK_ERROR, "签名验证失败");
        }

        if (request.getStatus() == 1) {
            // 支付成功
            pay.setPayStatus(1);
            pay.setTransactionId(request.getTransactionId());
            pay.setPayTime(LocalDateTime.now());
            pay.setCallbackContent(request.toString());
            payMapper.updateById(pay);

            // 调用订单服务，更新订单状态为已支付
            try {
                orderServiceClient.paySuccess(pay.getOrderNo());
                log.info("支付成功并更新订单状态: payNo={}, orderNo={}", request.getPayNo(), pay.getOrderNo());
            } catch (Exception e) {
                log.error("调用订单服务失败: payNo={}, orderNo={}", request.getPayNo(), pay.getOrderNo(), e);
                throw new BusinessException(ResultCode.PAY_CALLBACK_ERROR, "支付成功但更新订单失败");
            }

        } else {
            // 支付失败
            pay.setPayStatus(2);
            pay.setCallbackContent(request.toString());
            payMapper.updateById(pay);
            log.warn("支付失败: payNo={}", request.getPayNo());
        }
    }

    /**
     * 查询支付状态
     */
    public Pay queryPayStatus(String orderNo) {
        Long userId = UserContext.getUserId();
        return payMapper.selectOne(
                new LambdaQueryWrapper<Pay>()
                        .eq(Pay::getOrderNo, orderNo)
                        .eq(Pay::getUserId, userId)
                        .orderByDesc(Pay::getCreateTime)
                        .last("LIMIT 1"));
    }

    private String generatePayNo() {
        return "P" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private String generatePayUrl(String payNo) {
        // 模拟支付链接
        return "https://pay.weixin.qq.com/mock?payNo=" + payNo;
    }

    private boolean verifySign(PayCallbackRequest request) {
        // TODO: 实现签名验证逻辑
        return true;
    }
}
