package com.zhenyu.train.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhenyu.train.common.result.Result;
import com.zhenyu.train.order.dto.TicketResultResponse;
import com.zhenyu.train.order.dto.TicketSubmitRequest;
import com.zhenyu.train.order.dto.TicketSubmitResponse;
import com.zhenyu.train.order.entity.Order;
import com.zhenyu.train.order.entity.OrderDetail;
import com.zhenyu.train.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 提交购票请求
     */
    @PostMapping("/ticket/submit")
    public Result<TicketSubmitResponse> submitTicket(@Valid @RequestBody TicketSubmitRequest request) {
        TicketSubmitResponse response = orderService.submitTicket(request);
        return Result.success(response);
    }

    /**
     * 查询购票结果
     */
    @GetMapping("/ticket/result/{requestNo}")
    public Result<TicketResultResponse> getTicketResult(@PathVariable("requestNo") String requestNo) {
        TicketResultResponse response = orderService.getTicketResult(requestNo);
        return Result.success(response);
    }

    /**
     * 订单列表
     */
    @GetMapping("/list")
    public Result<Page<Order>> getOrderList(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<Order> result = orderService.getOrderList(status, page, size);
        return Result.success(result);
    }

    /**
     * 订单详情
     */
    @GetMapping("/detail/{orderNo}")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable("orderNo") String orderNo) {
        Order order = orderService.getOrderDetail(orderNo);
        List<OrderDetail> details = orderService.getOrderDetails(order.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("details", details);
        return Result.success(result);
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderNo}")
    public Result<Void> cancelOrder(@PathVariable("orderNo") String orderNo) {
        orderService.cancelOrder(orderNo);
        return Result.success();
    }

    /**
     * 退票
     */
    @PostMapping("/refund/{orderNo}")
    public Result<Void> refundOrder(@PathVariable("orderNo") String orderNo) {
        orderService.refundOrder(orderNo);
        return Result.success();
    }

    /**
     * 支付成功回调（供支付服务调用）
     */
    @PostMapping("/pay/success")
    public Result<Void> paySuccess(@RequestParam("orderNo") String orderNo) {
        orderService.paySuccess(orderNo);
        return Result.success();
    }

    /**
     * 获取订单信息（供支付服务调用）
     */
    @GetMapping("/info/{orderNo}")
    public java.util.Map<String, Object> getOrderInfo(@PathVariable("orderNo") String orderNo) {
        Order order = orderService.getOrderDetail(orderNo);
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", order.getOrderNo());
        result.put("userId", order.getUserId());
        result.put("totalAmount", order.getTotalAmount());
        result.put("orderStatus", order.getOrderStatus());
        return result;
    }
}
