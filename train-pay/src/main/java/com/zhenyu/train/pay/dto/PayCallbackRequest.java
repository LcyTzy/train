package com.zhenyu.train.pay.dto;

import lombok.Data;

/**
 * 支付回调请求
 */
@Data
public class PayCallbackRequest {

    /**
     * 支付流水号
     */
    private String payNo;

    /**
     * 第三方交易号
     */
    private String transactionId;

    /**
     * 支付状态: 1-成功 2-失败
     */
    private Integer status;

    /**
     * 签名
     */
    private String sign;
}
