package com.zhenyu.train.train.controller;

import com.zhenyu.train.common.result.Result;
import com.zhenyu.train.train.dto.TrainQueryRequest;
import com.zhenyu.train.train.dto.TrainQueryResponse;
import com.zhenyu.train.train.entity.Station;
import com.zhenyu.train.train.entity.Train;
import com.zhenyu.train.train.entity.TrainStation;
import com.zhenyu.train.train.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车次控制器
 */
@RestController
@RequestMapping("/train")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;

    /**
     * 获取车站列表
     */
    @GetMapping("/station/list")
    public Result<List<Station>> getStationList(@RequestParam(value = "keyword", required = false) String keyword) {
        List<Station> list = trainService.getStationList(keyword);
        return Result.success(list);
    }

    /**
     * 查询车次
     */
    @GetMapping("/query")
    public Result<List<TrainQueryResponse>> queryTrains(
            @RequestParam(value = "startStation", required = false) String startStation,
            @RequestParam(value = "endStation", required = false) String endStation,
            @RequestParam(value = "trainDate", required = false) LocalDate trainDate,
            @RequestParam(value = "trainType", required = false) Integer trainType,
            @RequestParam(value = "departTimeStart", required = false) String departTimeStart,
            @RequestParam(value = "departTimeEnd", required = false) String departTimeEnd) {

        // 不传站点时返回所有车次
        if (startStation == null || endStation == null) {
            List<TrainQueryResponse> list = trainService.getAllTrains(trainType);
            return Result.success(list);
        }

        TrainQueryRequest request = new TrainQueryRequest();
        request.setStartStation(startStation);
        request.setEndStation(endStation);
        request.setTrainDate(trainDate);
        request.setTrainType(trainType);
        if (departTimeStart != null) {
            request.setDepartTimeStart(java.time.LocalTime.parse(departTimeStart));
        }
        if (departTimeEnd != null) {
            request.setDepartTimeEnd(java.time.LocalTime.parse(departTimeEnd));
        }

        List<TrainQueryResponse> list = trainService.queryTrains(request);
        return Result.success(list);
    }

    /**
     * 车次详情(经停站信息)
     */
    @GetMapping("/detail/{trainId}")
    public Result<List<TrainStation>> getTrainDetail(@PathVariable("trainId") Long trainId) {
        List<TrainStation> list = trainService.getTrainDetail(trainId);
        return Result.success(list);
    }

    /**
     * 获取车次信息（供Feign调用）
     */
    @GetMapping("/info/{trainId}")
    public Map<String, Object> getTrainInfo(@PathVariable("trainId") Long trainId) {
        Train train = trainService.getTrainById(trainId);
        if (train == null) {
            return new HashMap<>();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("trainId", train.getId());
        result.put("trainNo", train.getTrainNo());
        result.put("trainType", train.getTrainType());
        result.put("startTime", train.getStartTime());
        result.put("arriveTime", train.getArriveTime());
        result.put("duration", train.getDuration());
        return result;
    }

    /**
     * 获取站点信息（供Feign调用）
     */
    @GetMapping("/station/info/{stationId}")
    public Map<String, Object> getStationInfo(@PathVariable("stationId") Long stationId) {
        Station station = trainService.getStationById(stationId);
        if (station == null) {
            return new HashMap<>();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("stationId", station.getId());
        result.put("stationName", station.getName());
        result.put("stationCode", station.getCode());
        return result;
    }

    /**
     * 获取车次站点信息（供Feign调用）
     */
    @GetMapping("/train/station/info")
    public Map<String, Object> getTrainStationInfo(@RequestParam("trainId") Long trainId, @RequestParam("stationId") Long stationId) {
        TrainStation trainStation = trainService.getTrainStation(trainId, stationId);
        if (trainStation == null) {
            return new HashMap<>();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("sequence", trainStation.getSequence());
        result.put("arriveTime", trainStation.getArriveTime());
        result.put("departTime", trainStation.getDepartTime());
        return result;
    }
}
