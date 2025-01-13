package com.wegame.capi.api.domain.res.wegamego;

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
public class WeGameGoCommonRes<T> implements Serializable {
    private static final long serialVersionUID = 4567899887668567L;
    private int code;
    private String message;
    private T data;

    public boolean success() {
        return Objects.nonNull(data) && code == 1;
    }
}
