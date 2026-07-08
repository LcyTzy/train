package com.zhenyu.train.pay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 支付创建请求
 */
@Data
public class PayCreateRequest {

    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    /**
     * 支付渠道: 1-微信 2-支付宝
     */
    private Integer payChannel = 1;
}
