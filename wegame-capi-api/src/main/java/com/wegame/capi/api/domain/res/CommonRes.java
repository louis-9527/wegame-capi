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
public class CommonRes<T> implements Serializable {

    private static final long serialVersionUID = -636301797303418L;
    public static final String STATUS_SUCCESS = "success";
    public static final int SUC_CODE = 200;

    /**
     * 响应码: 200成功
     */
    private Integer status;

    private String message;
    /**
     * 响应消息
     */
    private String errorMessage;
    /**
     * 响应数据
     */
    private T data;

    public boolean isSuc() {
        return Objects.nonNull(status) && status == SUC_CODE;
    }
}


