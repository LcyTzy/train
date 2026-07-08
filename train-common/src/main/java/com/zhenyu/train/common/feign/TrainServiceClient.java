package com.zhenyu.train.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 车次服务Feign客户端
 */
@FeignClient(name = "train-train", fallbackFactory = TrainServiceClientFallbackFactory.class)
public interface TrainServiceClient {

    /**
     * 获取车次信息
     */
    @GetMapping("/train/info/{trainId}")
    Map<String, Object> getTrainInfo(@PathVariable("trainId") Long trainId);

    /**
     * 获取站点信息
     */
    @GetMapping("/station/info/{stationId}")
    Map<String, Object> getStationInfo(@PathVariable("stationId") Long stationId);

    /**
     * 获取车次站点信息
     */
    @GetMapping("/train/station/info")
    Map<String, Object> getTrainStationInfo(@RequestParam("trainId") Long trainId,
                                             @RequestParam("stationId") Long stationId);
}
