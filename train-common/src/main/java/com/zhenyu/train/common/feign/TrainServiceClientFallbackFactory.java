package com.zhenyu.train.common.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * 车次服务降级处理
 */
@Slf4j
@Component
public class TrainServiceClientFallbackFactory implements FallbackFactory<TrainServiceClient> {

    @Override
    public TrainServiceClient create(Throwable cause) {
        log.error("车次服务调用失败: {}", cause.getMessage());
        return new TrainServiceClient() {
            @Override
            public Map<String, Object> getTrainInfo(Long trainId) {
                return Collections.emptyMap();
            }

            @Override
            public Map<String, Object> getStationInfo(Long stationId) {
                return Collections.emptyMap();
            }

            @Override
            public Map<String, Object> getTrainStationInfo(Long trainId, Long stationId) {
                return Collections.emptyMap();
            }
        };
    }
}
