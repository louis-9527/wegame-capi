package com.wegame.capi.biz.domain.biz.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 背包当前奖励DO
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BagOtherRewordDetailDo {
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
     * 跳转时游戏ID
     */
    private String skipGameId;
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
}
