package com.zhenyu.train.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 乘客实体(乘车人)
 */
@Data
@TableName("t_passenger")
public class Passenger {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 乘客姓名
     */
    private String name;

    /**
     * 证件类型: 0-身份证 1-护照 2-军官证
     */
    private Integer idType;

    /**
     * 证件号码
     */
    private String idNumber;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 乘客类型: 0-成人 1-儿童 2-学生 3-残疾军人
     */
    private Integer passengerType;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
