package com.wegame.capi.biz.domain.business.bet;

import com.wegame.base.enums.BooleanEnum;
import com.wegame.capi.common.enums.GameRewardAmountTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameBetResultDetailDo {
    private Integer id;
    private Long betAmount;
    private Long rewardAmount;
    private BigDecimal multiple;
    private Integer[][] gameResult;
    private GameRewardAmountTypeEnum rewardAmountType;
    /**
     * 是否中了免费轮
     */
    private BooleanEnum freeSpinsFlag;
    /**
     * 免费轮次数
     */
    private Integer freeSpinsTimes;
    private List<RewardResult> rewardResults;


    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class RewardResult{
        /**
         * 元素id
         */
        private Integer symbolId;
        private BigDecimal multiple;
        private Long rewardAmount;
        /**
         * 赔率
         */
        private BigDecimal odds;
        private Integer[][] rewardIndex;

        /**
         * 当前中奖线的中奖id列表
         */
        private List<Integer> rewardSymbolIdList;
    }
}
