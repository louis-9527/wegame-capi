package com.wegame.capi.api.domain.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.client.domain.request.UserLoginRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.slf4j.MDC;

import java.io.Serializable;
import java.util.Date;

/**
 * 验证主站登录token请求模型
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GetAuthLoginTokenReq implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 平台id
     */
    @JsonProperty("pId")
    private String pId;
    /**
     * 渠道id
     */
    private String channelId;
    /**
     * 玩家姓名
     */
    private String playerName;
    /**
     * request hash
     */
    private String hash;
    /**
     * request refId
     */
    private String refId;
    /**
     * request createTime
     */
    private String createTime;
    /**
     * cp主站token
     */
    private String token;

    public static GetAuthLoginTokenReq buildClientReq(UserLoginRequest request) {
        return GetAuthLoginTokenReq.builder()
                .pId(request.getPlatformId())
                .channelId(request.getChanelId())
                .playerName(request.getPlayerName())
                .createTime(DateUtil.toUTC(new Date()))
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .token(request.getToken())
                .build();
    }
}
