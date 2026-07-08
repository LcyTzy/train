package com.zhenyu.train.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 订单服务Feign客户端
 */
@FeignClient(name = "train-order", fallbackFactory = OrderServiceClientFallbackFactory.class)
public interface OrderServiceClient {

    /**
     * 支付成功回调
     */
    @PostMapping("/order/pay/success")
    void paySuccess(@RequestParam("orderNo") String orderNo);

    /**
     * 获取订单信息（供支付服务调用）
     */
    @GetMapping("/order/info/{orderNo}")
    Map<String, Object> getOrderInfo(@PathVariable("orderNo") String orderNo);
}
