package com.wegame.capi.api.domain.res.singlewallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * 验证主站登录token响应模型
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AuthLoginTokenRes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * refId
     */
    private String refId;
    /**
     * 错误请求数据
     */
    private List<ErrorMessages> errorMessages;

    @Data
    static class ErrorMessages{
        private String property;
        private String message;
    }
}
