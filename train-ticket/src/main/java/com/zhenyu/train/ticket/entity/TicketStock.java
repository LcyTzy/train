package com.zhenyu.train.ticket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 余票库存实体
 */
@Data
@TableName("t_ticket_stock")
public class TicketStock {

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
     * 座位类型
     */
    private Integer seatType;

    /**
     * 总票数
     */
    private Integer totalCount;

    /**
     * 可用票数
     */
    private Integer availableCount;

    /**
     * 已售票数
     */
    private Integer soldCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
