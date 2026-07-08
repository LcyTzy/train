package com.zhenyu.train.common.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TicketServiceClientFallbackFactory implements FallbackFactory<TicketServiceClient> {

    @Override
    public TicketServiceClient create(Throwable cause) {
        log.error("余票服务调用失败: {}", cause.getMessage());
        return new TicketServiceClient() {
            @Override
            public boolean deductStock(Long trainId, String trainDate, Integer carriageNo, Integer seatType, Integer count) {
                return false;
            }

            @Override
            public void restoreStock(Long trainId, String trainDate, Integer carriageNo, Integer seatType, Integer count) {
            }
        };
    }
}
