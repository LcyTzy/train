package com.zhenyu.train.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有操作权限"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    // 业务错误 5xx
    ERROR(500, "系统内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    // 用户相关 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_ALREADY_EXISTS(1003, "用户名已存在"),
    USER_PHONE_EXISTS(1004, "手机号已注册"),
    USER_NOT_VERIFY(1005, "用户未进行身份核验"),

    // 车次相关 2xxx
    TRAIN_NOT_FOUND(2001, "车次不存在"),
    TRAIN_SOLD_OUT(2002, "车次已售罄"),
    TRAIN_STOPPED(2003, "车次已停运"),

    // 余票相关 3xxx
    TICKET_NOT_ENOUGH(3001, "余票不足"),
    TICKET_STOCK_NOT_FOUND(3002, "库存信息不存在"),
    TICKET_SEAT_OCCUPIED(3003, "座位已被占用"),

    // 订单相关 4xxx
    ORDER_NOT_FOUND(4001, "订单不存在"),
    ORDER_STATUS_ERROR(4002, "订单状态异常"),
    ORDER_ALREADY_PAID(4003, "订单已支付"),
    ORDER_EXPIRED(4004, "订单已超时"),
    ORDER_CANNOT_CANCEL(4005, "订单无法取消"),
    ORDER_CANNOT_REFUND(4006, "订单无法退票"),

    // 支付相关 5xxx
    PAY_AMOUNT_ERROR(5001, "支付金额错误"),
    PAY_CALLBACK_ERROR(5002, "支付回调处理失败"),
    PAY_TIMEOUT(5003, "支付超时");

    private final Integer code;
    private final String message;
}
