package com.wegame.capi.api.domain.res.go;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;

/**
 * wegame-go 响应参数
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GoCommonRes<T> implements Serializable {
    private static final long serialVersionUID = 4567899887668567L;
    private static final Integer SUC_CODE = 1;
    private Integer code;
    private String message;
    private T data;

    public boolean isSuccess() {
        return Objects.nonNull(data) && Objects.equals(code, SUC_CODE);
    }
}
