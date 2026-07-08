package com.zhenyu.train.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付实体
 */
@Data
@TableName("t_pay")
public class Pay {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 支付流水号
     */
    private String payNo;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付渠道: 1-微信支付 2-支付宝
     */
    private Integer payChannel;

    /**
     * 支付状态: 0-待支付 1-支付成功 2-支付失败 3-已退款
     */
    private Integer payStatus;

    /**
     * 第三方交易号
     */
    private String transactionId;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 回调内容(JSON)
     */
    private String callbackContent;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
