package com.zhenyu.train.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细实体
 */
@Data
@TableName("t_order_detail")
public class OrderDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 乘客ID
     */
    private Long passengerId;

    /**
     * 乘客姓名
     */
    private String passengerName;

    /**
     * 证件类型
     */
    private Integer passengerIdType;

    /**
     * 证件号码
     */
    private String passengerIdNumber;

    /**
     * 车厢号
     */
    private Integer carriageNo;

    /**
     * 座位号
     */
    private String seatNo;

    /**
     * 座位类型
     */
    private Integer seatType;

    /**
     * 票价
     */
    private BigDecimal price;

    /**
     * 票状态: 0-正常 1-已退票 2-已改签
     */
    private Integer ticketStatus;

    /**
     * 退票时间
     */
    private LocalDateTime refundTime;

    private LocalDateTime createTime;
}
