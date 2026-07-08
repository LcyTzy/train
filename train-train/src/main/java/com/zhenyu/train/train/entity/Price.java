package com.zhenyu.train.train.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 票价实体
 */
@Data
@TableName("t_price")
public class Price {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 车次ID
     */
    private Long trainId;

    /**
     * 始发站ID
     */
    private Long startStationId;

    /**
     * 终点站ID
     */
    private Long endStationId;

    /**
     * 座位类型
     */
    private Integer seatType;

    /**
     * 票价
     */
    private BigDecimal price;

    private LocalDateTime createTime;
}
