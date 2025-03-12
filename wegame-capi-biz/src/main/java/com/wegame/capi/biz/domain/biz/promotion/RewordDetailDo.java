package com.wegame.capi.biz.domain.biz.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RewordDetailDo {
    /**
     * 奖励名称
     */
    private String name;
    /**
     * 奖励描述
     */
    private String description;
    /**
     * 使用余额要求
     */
    private Long balanceRequired;
    /**
     * 奖励URL
     */
    private String imgUrl;

    /**
     * 游戏ID,需要传给前端,做游戏跳转
     */
    private String gameId;

    /**
     * 游戏内部ID
     */
    private String gameCode;

    /**
     * 游戏名称
     */
    private String gameName;

    /**
     * 奖励类型(来源)
     */
    private String rewardType;

    /**
     * 奖励投注金额
     */
    private Long betValue;
    /**
     * 开始日期
     */
    private Integer starLevel;
    /**
     * Reward valid day
     */
    private Integer periodDay;
    /**
     * Reward valid hour
     */
    private Integer PeriodHour;
}
