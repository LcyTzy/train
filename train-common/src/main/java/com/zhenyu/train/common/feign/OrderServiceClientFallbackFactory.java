package com.zhenyu.train.common.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderServiceClientFallbackFactory implements FallbackFactory<OrderServiceClient> {

    @Override
    public OrderServiceClient create(Throwable cause) {
        log.error("订单服务调用失败: {}", cause.getMessage());
        return new OrderServiceClient() {
            @Override
            public void paySuccess(String orderNo) {
                log.error("支付成功回调失败: {}", orderNo, cause);
            }
            
            @Override
            public java.util.Map<String, Object> getOrderInfo(String orderNo) {
                log.error("获取订单信息失败: {}", orderNo, cause);
                return new java.util.HashMap<>();
            }
        };
    }
}
