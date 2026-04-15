package com.wegame.capi.biz.domain.biz.promotion;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 背包中其他游戏奖励明细DO
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BagOtherRewardDetailDo implements Serializable {
    /**
     * 奖励ID
     */
    private String rewardId;
    /**
     * 奖励名称
     */
    private String name;
    /**
     * 奖励描述
     */
    private String description;
    /**
     * 游戏code
     */
    private String gameCode;
    /**
     * 系统游戏ID
     */
    private String gameId;
    /**
     * 游戏名称
     */
    private String gameName;
    /**
     * 图标URL
     */
    private String imgUrl;
    /**
     * 奖励总数
     */
    private Integer totalCount;
    /**
     * is Free Bet
     */
    private Boolean isFreeBet;
}
