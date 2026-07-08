package com.zhenyu.train.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("t_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码(BCrypt加密)
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

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
     * 邮箱
     */
    private String email;

    /**
     * 核验状态: 0-未核验 1-已核验
     */
    private Integer verifyStatus;

    /**
     * 状态: 0-禁用 1-正常
     */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
