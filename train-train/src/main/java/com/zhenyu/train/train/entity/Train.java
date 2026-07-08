package com.zhenyu.train.train.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 车次实体
 */
@Data
@TableName("t_train")
public class Train {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 车次号
     */
    private String trainNo;

    /**
     * 车次类型: 1-高铁 2-动车 3-直达 4-特快 5-快速
     */
    private Integer trainType;

    /**
     * 始发站ID
     */
    private Long startStationId;

    /**
     * 终点站ID
     */
    private Long endStationId;

    /**
     * 发车时间
     */
    private LocalTime startTime;

    /**
     * 到达时间
     */
    private LocalTime arriveTime;

    /**
     * 运行时长(分钟)
     */
    private Integer duration;

    /**
     * 状态: 0-停运 1-正常
     */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
