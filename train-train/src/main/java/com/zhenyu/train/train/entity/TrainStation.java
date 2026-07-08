package com.zhenyu.train.train.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 车次站点实体
 */
@Data
@TableName("t_train_station")
public class TrainStation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 车次ID
     */
    private Long trainId;

    /**
     * 车站ID
     */
    private Long stationId;

    /**
     * 站序
     */
    private Integer sequence;

    /**
     * 到达时间
     */
    private LocalTime arriveTime;

    /**
     * 出发时间
     */
    private LocalTime departTime;

    /**
     * 停靠时长(分钟)
     */
    private Integer stopMinutes;

    /**
     * 距始发站里程(km)
     */
    private Integer distance;

    private LocalDateTime createTime;
}
