package com.wegame.capi.biz.domain.biz;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolIndexDo;
import com.wegame.capi.common.enums.PrizeTypeEnum;
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
    /**
     * 投注金额
     */
    private Long betAmount;
    /**
     * 中奖总金额
     */
    private Long rewardAmount;
    /**
     * 倍率
     */
    private BigDecimal multiple;
    /**
     * 奖励类型
     */
    private PrizeTypeEnum prizeType;
    /**
     * 是否中了免费轮
     */
    private boolean freeSpinsFlag;
    /**
     * 游戏结果
     */
    private SymbolDo[][] gameResult;
    /**
     * 中奖结果列表
     */
    private List<RewardResult> rewardResults;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class RewardResult {
        /**
         * 元素id
         */
        private SymbolDo symbol;
        /**
         * 倍率
         */
        private BigDecimal multiple;
        /**
         * 中奖金额
         */
        private Long rewardAmount;
        /**
         * 赔率
         */
        private BigDecimal odds;
        /**
         * 中奖线列表
         */
        private List<SymbolIndexDo> rewardIndex;
        /**
         * 当前中奖线元素集合
         */
        private List<SymbolDo> rewardSymbolList;
    }
}
