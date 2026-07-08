package com.zhenyu.train.train.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

/**
 * 车次查询响应
 */
@Data
public class TrainQueryResponse {

    private Long trainId;
    private String trainNo;
    private Integer trainType;
    private String trainTypeDesc;
    private String startStationName;
    private String endStationName;
    private LocalTime departTime;
    private LocalTime arriveTime;
    private Integer duration;

    /**
     * 座位价格列表
     */
    private List<SeatPrice> seatPrices;

    @Data
    public static class SeatPrice {
        private Integer seatType;
        private String seatName;
        private BigDecimal price;
        private Integer availableCount;
    }
}
