package com.wegame.capi.api.domain.req.singlewallet;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 验证主站登录token请求模型
 */
@Data
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GetAuthLoginTokenReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 1L;
    private String token;

}
