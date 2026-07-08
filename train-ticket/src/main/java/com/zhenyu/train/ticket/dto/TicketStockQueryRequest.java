package com.zhenyu.train.ticket.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 余票查询请求
 */
@Data
public class TicketStockQueryRequest {

    /**
     * 车次ID
     */
    private Long trainId;

    /**
     * 乘车日期
     */
    private LocalDate trainDate;

    /**
     * 出发站ID
     */
    private Long startStationId;

    /**
     * 到达站ID
     */
    private Long endStationId;
}
