package com.zhenyu.train.common.constant;

/**
 * Redis Key 常量
 */
public class RedisKeyConstant {

    /**
     * 用户Token前缀
     */
    public static final String USER_TOKEN_PREFIX = "user:token:";

    /**
     * 用户信息前缀
     */
    public static final String USER_INFO_PREFIX = "user:info:";

    /**
     * 余票库存前缀 (key: ticket:stock:{trainId}:{date}:{carriageNo}:{seatType})
     */
    public static final String TICKET_STOCK_PREFIX = "ticket:stock:";

    /**
     * 座位锁定前缀 (key: seat:lock:{trainId}:{date}:{carriageNo}:{seatNo})
     */
    public static final String SEAT_LOCK_PREFIX = "seat:lock:";

    /**
     * 订单锁前缀 (key: order:lock:{orderNo})
     */
    public static final String ORDER_LOCK_PREFIX = "order:lock:";

    /**
     * 支付锁前缀 (key: pay:lock:{payNo})
     */
    public static final String PAY_LOCK_PREFIX = "pay:lock:";

    /**
     * 验证码前缀
     */
    public static final String VERIFY_CODE_PREFIX = "verify:code:";

    /**
     * 限流前缀
     */
    public static final String RATE_LIMIT_PREFIX = "rate:limit:";
}
