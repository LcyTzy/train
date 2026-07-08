package com.zhenyu.train.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "train-user", fallbackFactory = UserServiceClientFallbackFactory.class)
public interface UserServiceClient {

    /**
     * 获取乘客信息
     */
    @GetMapping("/user/passenger/info/{id}")
    Map<String, Object> getPassengerInfo(@PathVariable("id") Long id);
}
