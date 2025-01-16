package com.wegame.capi.api.domain.req.singlewallet;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 验证主站登录token请求模型
 */
@Data
@SuperBuilder
@ToString
public class GetAuthLoginTokenReq extends SingleWalletBaseReq implements Serializable {
    private static final long serialVersionUID = 1L;
    private String token;

}
