package com.wegame.capi.api.domain.res.singlewallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SingleWalletCommonRes<T> implements Serializable {
    private static final long serialVersionUID = -63633457303418L;
    public static final String STATUS_SUCCESS = "success";
    public static final int SUC_CODE = 200;

    /**
     * 响应码: 200成功
     */
    private Integer status;
    /**
     * 响应消息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;

    public boolean isSuc() {
        return Objects.nonNull(status) && Objects.equals(status, SUC_CODE) && Objects.nonNull(data);
    }
}
