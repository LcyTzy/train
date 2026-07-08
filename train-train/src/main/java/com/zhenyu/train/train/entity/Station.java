package com.zhenyu.train.train.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 车站实体
 */
@Data
@TableName("t_station")
public class Station {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 车站名称
     */
    private String name;

    /**
     * 车站编码
     */
    private String code;

    /**
     * 所属省份
     */
    private String province;

    /**
     * 所属城市
     */
    private String city;

    /**
     * 状态: 0-停用 1-正常
     */
    private Integer status;

    private LocalDateTime createTime;
}
