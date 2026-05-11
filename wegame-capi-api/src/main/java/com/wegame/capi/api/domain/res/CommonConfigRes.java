package com.wegame.capi.api.domain.res;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CommonConfigRes<T> implements Serializable {

    private static final long serialVersionUID = -636301797303418L;
    public static final String STATUS_SUCCESS = "success";
    public static final int SUC_CODE = 1;

    /**
     * 响应码: 200成功
     */
    private Integer code;
    private String message;
    /**
     * 响应数据
     */
    private T data;

    public boolean isSuc() {
        return Objects.nonNull(code) && code == SUC_CODE;
    }
}


