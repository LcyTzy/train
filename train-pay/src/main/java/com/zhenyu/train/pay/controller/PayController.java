package com.zhenyu.train.pay.controller;

import com.zhenyu.train.common.result.Result;
import com.zhenyu.train.pay.dto.PayCallbackRequest;
import com.zhenyu.train.pay.dto.PayCreateRequest;
import com.zhenyu.train.pay.dto.PayCreateResponse;
import com.zhenyu.train.pay.entity.Pay;
import com.zhenyu.train.pay.service.PayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    /**
     * 创建支付
     */
    @PostMapping("/create")
    public Result<PayCreateResponse> createPay(@Valid @RequestBody PayCreateRequest request) {
        PayCreateResponse response = payService.createPay(request);
        return Result.success(response);
    }

    /**
     * 支付回调
     */
    @PostMapping("/callback")
    public Result<Void> payCallback(@RequestBody PayCallbackRequest request) {
        payService.payCallback(request);
        return Result.success();
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/query/{orderNo}")
    public Result<Pay> queryPayStatus(@PathVariable("orderNo") String orderNo) {
        Pay pay = payService.queryPayStatus(orderNo);
        return Result.success(pay);
    }
}
