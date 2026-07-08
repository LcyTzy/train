package com.zhenyu.train.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 车次类型枚举
 */
@Getter
@AllArgsConstructor
public enum TrainTypeEnum {

    HIGH_SPEED(1, "高铁"),
    EMU(2, "动车"),
    DIRECT(3, "直达"),
    EXPRESS(4, "特快"),
    FAST(5, "快速");

    private final Integer code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        for (TrainTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type.getDesc();
            }
        }
        return "未知";
    }
}
