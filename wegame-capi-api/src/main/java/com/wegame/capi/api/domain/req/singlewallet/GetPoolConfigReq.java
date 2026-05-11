package com.wegame.capi.api.domain.req.singlewallet;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@ToString(callSuper = true)
@ApiModel("获取奖池配置请求模型")
public class GetPoolConfigReq implements Serializable {

    /**
     * 商户代码（即 providerId）
     */
    private String agentCode;
    /**
     * AES-256-CBC 加密后的业务报文（Hex 编码）
     */
    private String message;

    @Data
    @SuperBuilder
    @ToString(callSuper = true)
    @ApiModel("MessageBody请求模型")
    public static class MessageBody implements Serializable {
        /**
         * 指定游戏Code，仅返回该游戏关联的资金池；不传则返回该商户下所有资金池
         */
        private String gameCode;
        /**
         * 请求唯一标识（用于链路追踪）
         */
        private String refId;
    }

}
