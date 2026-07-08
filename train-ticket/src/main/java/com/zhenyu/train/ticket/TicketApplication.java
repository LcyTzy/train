package com.zhenyu.train.ticket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 余票服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@MapperScan("com.zhenyu.train.ticket.mapper")
@ComponentScan(basePackages = {"com.zhenyu.train.ticket", "com.zhenyu.train.common"})
public class TicketApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketApplication.class, args);
    }
}
