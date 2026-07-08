package com.zhenyu.train.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 购票消息
 */
@Data
public class TicketSubmitMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestNo;
    private Long userId;
    private Long trainId;
    private LocalDate trainDate;
    private Long startStationId;
    private Long endStationId;
    private Integer seatType;
    private List<Long> passengerIds;
}
