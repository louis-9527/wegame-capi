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
    /**
     * 响应状态码
     * 200:成功
     * 400:干预事件状态错误
     * 404:干预事件Id不存在
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

    public Boolean isSuccess() {
        return StringUtils.equals(SUC_CODE, status);
    }
}
