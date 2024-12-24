package com.wegame.capi.api.domain.req.singlewallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 单钱包基础请求模型
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SingleWalletBaseReq implements Serializable {
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
     * request refId
     */
    private String createTime;
}
