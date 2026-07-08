package com.zhenyu.train.ticket.dto;

import lombok.Data;

/**
 * 余票响应
 */
@Data
public class TicketStockResponse {

    /**
     * 座位类型
     */
    private Integer seatType;

    /**
     * 座位类型名称
     */
    private String seatName;

    /**
     * 可用票数
     */
    private Integer availableCount;

    /**
     * 票价
     */
    private java.math.BigDecimal price;
}
