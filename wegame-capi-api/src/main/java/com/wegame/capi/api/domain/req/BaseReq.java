package com.wegame.capi.api.domain.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 单钱包基础请求模型
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BaseReq implements Serializable {
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
}
