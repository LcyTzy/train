package com.zhenyu.train.ticket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 座位明细实体
 */
@Data
@TableName("t_seat")
public class Seat {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 车次ID
     */
    private Long trainId;

    /**
     * 乘车日期
     */
    private LocalDate trainDate;

    /**
     * 车厢号
     */
    private Integer carriageNo;

    /**
     * 座位号
     */
    private String seatNo;

    /**
     * 排号
     */
    private Integer seatRow;

    /**
     * 列号
     */
    private String seatCol;

    /**
     * 座位类型
     */
    private Integer seatType;

    /**
     * 状态: 0-可售 1-已售 2-锁定
     */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
