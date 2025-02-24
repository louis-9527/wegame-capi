package com.wegame.capi.api.domain.res.dynamicoperation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventsStatusRes implements Serializable {
    private static final long serialVersionUID = -4392682699211806649L;
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
    private String duplicatedBetNos;


    public Boolean isSuccess(){
        return Objects.equals("200",status);
    }
}
