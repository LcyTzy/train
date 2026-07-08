package com.zhenyu.train.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 订单服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients(basePackages = "com.zhenyu.train.common.feign")
@MapperScan("com.zhenyu.train.order.mapper")
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
