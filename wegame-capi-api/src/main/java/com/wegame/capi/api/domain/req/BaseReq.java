package com.wegame.capi.api.domain.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 基础请求模型
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BaseReq implements Serializable {
    /**
     * 请求 ID
     */
    private String requestId;
    /**
     * 平台 ID
     */
    private String platformId;
    /**
     * 代理商 ID
     */
    private String agentId;
    /**
     * 签名
     */
    private String signature;

}
