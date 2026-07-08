package com.zhenyu.train.pay.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付创建响应
 */
@Data
public class PayCreateResponse {

    /**
     * 支付流水号
     */
    private String payNo;

    /**
     * 支付链接/二维码URL
     */
    private String payUrl;

    /**
     * 支付金额
     */
    private BigDecimal amount;
}
