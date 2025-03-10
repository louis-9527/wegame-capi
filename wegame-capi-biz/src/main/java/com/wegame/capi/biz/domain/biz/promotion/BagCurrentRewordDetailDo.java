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
public class BagCurrentRewordDetailDo {
    /**
     * 奖励ID
     */
    private String rewardId;
    /**
     * 奖励名称
     */
    private String name;

    /**
     * 等级
     */
    private Integer starLevel;

    /**
     * 奖励描述
     */
    private String description;
    /**
     * 余额要求
     */
    private Long balanceRequired;
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
     * 生效时间
     */
    private Long effectTime;
    /**
     * 过期时间
     */
    private Long expiredTime;
    /**
     * 奖励类型,1:每日任务 2:宝箱 4:成就
     */
    private String rewardType;

    /**
     * 投注基本金额
     */
    private Long betValue;
    /**
     * 图标URL
     */
    private String imgUrl;
}
