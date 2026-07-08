package com.zhenyu.train.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 订单实体
 */
@Data
@TableName("t_order")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 车次ID
     */
    private Long trainId;

    /**
     * 车次号
     */
    private String trainNo;

    /**
     * 乘车日期
     */
    private LocalDate trainDate;

    /**
     * 出发站ID
     */
    private Long startStationId;

    /**
     * 出发站名称
     */
    private String startStationName;

    /**
     * 到达站ID
     */
    private Long endStationId;

    /**
     * 到达站名称
     */
    private String endStationName;

    /**
     * 发车时间
     */
    private LocalTime departTime;

    /**
     * 到达时间
     */
    private LocalTime arriveTime;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 乘客人数
     */
    private Integer passengerCount;

    /**
     * 订单状态: 0-待支付 1-已支付 2-出票成功 3-已退票 4-已改签 5-已取消 6-支付超时
     */
    private Integer orderStatus;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 支付过期时间
     */
    private LocalDateTime expireTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
