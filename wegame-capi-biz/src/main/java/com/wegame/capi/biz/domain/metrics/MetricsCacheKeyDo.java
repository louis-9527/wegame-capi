package com.wegame.capi.biz.domain.metrics;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 用户玩法指标缓存key 信息
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MetricsCacheKeyDo implements Serializable {
    private static final long serialVersionUID = 3202834320673L;
    /**
     * 平台id
     */
    private String pId;
    /**
     * 渠道id
     */
    private String channelId;
    /**
     * 玩家id
     */
    private String playerId;
    /**
     * 玩家name
     */
    private String playerName;
}
