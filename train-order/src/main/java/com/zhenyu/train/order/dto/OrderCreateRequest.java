package com.zhenyu.train.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 订单创建请求
 */
@Data
public class OrderCreateRequest {

    private Long userId;
    private Long trainId;
    private String trainNo;
    private LocalDate trainDate;
    private Long startStationId;
    private String startStationName;
    private Long endStationId;
    private String endStationName;
    private LocalTime departTime;
    private LocalTime arriveTime;
    private BigDecimal totalAmount;
    private Integer passengerCount;
    private List<OrderDetailCreateRequest> details;
}
