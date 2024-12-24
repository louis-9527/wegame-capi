package com.wegame.capi.api.domain.res;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BaseRes implements Serializable {
    static final long serialVersionUID = -8519013691660936626L;

    public static final int SUC_CODE = 200;

    /**
     * 接口请求成功 200
     */
    private Integer status;

    /**
     * requestId
     */
    private String requestId;

    /**
     * 错误信息
     */
    private String errorMessage;

    public boolean isSuc() {
        return Objects.nonNull(status) && status == SUC_CODE;
    }

}
