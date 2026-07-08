package com.zhenyu.train.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 购票请求
 */
@Data
public class TicketSubmitRequest {

    @NotNull(message = "车次ID不能为空")
    private Long trainId;

    @NotNull(message = "乘车日期不能为空")
    private LocalDate trainDate;

    @NotNull(message = "出发站ID不能为空")
    private Long startStationId;

    @NotNull(message = "到达站ID不能为空")
    private Long endStationId;

    @NotNull(message = "座位类型不能为空")
    private Integer seatType;

    @NotEmpty(message = "乘客列表不能为空")
    private List<Long> passengerIds;
}
