package com.zhenyu.train.train;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 车次服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.zhenyu.train.train.mapper")
@ComponentScan(basePackages = {"com.zhenyu.train.train", "com.zhenyu.train.common"})
public class TrainApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainApplication.class, args);
    }
}
