package com.wegame.capi.api.domain.res.promotion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RedeemPlayerCodeRes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 玩家名称
     */
    private String playerName;
    /**
     * 渠道id
     */
    private String channelId;
    /**
     * 平台id
     */
    @JsonProperty("pId")
    private String pId;
    /**
     * refId
     */
    private String refId;
    /**
     * 兑换码
     */
    private String code;
    /**
     * 奖励类型: 1: starCard / 2: freeSpin
     */
    private String rewardType;
    /**
     * 奖励对象 (Free Spin 或 Star Card)
     */
    private Object reward;
}
