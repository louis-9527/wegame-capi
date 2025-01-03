package com.wegame.capi.biz.domain.business;

import com.wegame.base.enums.BooleanEnum;
import com.wegame.capi.common.enums.GameGrandPrizeTypeEnum;
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
    private GameGrandPrizeTypeEnum rewardAmountType;
    /**
     * 是否中了免费轮
     */
    private BooleanEnum freeSpinsFlag;

    private SymbolDo[][] gameResult;
    //中奖结果(多条线)
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
        private SymbolDo symbol;
        private BigDecimal multiple;
        private Long rewardAmount;
        /**
         * 赔率
         */
        private BigDecimal odds;
        //中奖线
        private List<SymbolIndexDo> rewardIndex;

        /**
         * 当前中奖线的中奖id列表
         */
        private List<SymbolDo> rewardSymbolList;
    }
}
