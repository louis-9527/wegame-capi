package com.wegame.capi.api.domain.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.singlewallet.SingleWalletBaseReq;
import com.wegame.capi.client.domain.request.UserLoginRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.slf4j.MDC;

import java.io.Serializable;
import java.util.Date;

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
