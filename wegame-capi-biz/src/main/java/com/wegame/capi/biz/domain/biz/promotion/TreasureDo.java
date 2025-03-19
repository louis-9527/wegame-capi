package com.wegame.capi.biz.domain.biz.promotion;

import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "宝箱响应模型")
public class TreasureDo implements  Serializable {
    /**
     * 宝箱ID
     */
    private String treasureId;
    /**
     * 宝箱名称
     */
    private String name;
    /**
     * 当前进度
     */
    private BigDecimal currentExperience;
    /**
     * 目标进度
     */
    private BigDecimal targetExperience;
    /**
     * 状态:1进行中 2可兑换 3已兑换
     */
    private Integer status;
    /**
     * 宝箱等级
     */
    private Integer vipLevel;
    /**
     * 宝箱奖励列表
     */
    private List<Reward> rewards;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ApiModel(description = "宝箱奖励")
    public static class Reward implements Serializable {
        private static final long serialVersionUID = 7692352397871708565L;
        /**
         * 奖励ID
         */
        private String rewardId;
        /**
         * 奖励名称
         */
        private String rewardName;
        /**
         * 奖励次数
         */
        private Integer rewardCount;
        /**
         * 奖励等级
         */
        private Integer starLevel;
        /**
         * icon地址
         */
        private String imgUrl;
    }
}