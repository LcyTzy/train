package com.zhenyu.train.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 身份核验请求
 */
@Data
public class VerifyRequest {

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotNull(message = "证件类型不能为空")
    private Integer idType;

    @NotBlank(message = "证件号码不能为空")
    private String idNumber;
}
