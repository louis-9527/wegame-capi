package com.wegame.capi.api.domain.res.dynamicoperation;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UpdateEventsStatusRes implements Serializable {
    private static final long serialVersionUID = -4392682699211806649L;
    private static final String SUC_CODE = "200";
    private static final String FINISH_CODE = "409";
    private static final String NOT_EXISTS_CODE = "404";

    /**
     * 响应状态码
     * 200:成功
     * 400:干预事件状态错误
     * 404:干预事件Id不存在
     * 409:干预事件已完成
     * 500:系统错误
     */
    private String status;
    /**
     * 响应消息
     */
    private String message;
    /**
     * 已发送过的订单号列表
     */
    private List<String> duplicatedBetNos;

    public boolean isSuccess() {
        return StringUtils.equals(SUC_CODE, status) || StringUtils.equals(FINISH_CODE, status);
    }

    public Boolean isFail() {
        if (StringUtils.equals(NOT_EXISTS_CODE, status)) {
            return true;
        }
        return false;
    }
}
