package com.wegame.capi.biz.domain.biz;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolIndexDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.common.enums.RewardSubTypeEnum;
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
public class MariaMakilingGameRecordDetailDo {
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
    private RewardSubTypeEnum prizeType;
    /**
     * 是否中了免费轮
     */
    private boolean freeSpinsFlag;
    /**
     * 游戏结果
     * todo delete
     */
    private SymbolDo[][] gameResult;
    /**
     * 滚轮
     */
    private WheelDo wheel;
    /**
     * 中奖结果列表
     */
    private List<RewardLineResultDo> rewardResults;

    /**
     * 是否最大免费轮id
     */
    private boolean maxFreeSpinsIdFlag;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class RewardLineResultDo {
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
         * 中奖线元素列表
         */
        private List<SymbolDo> rewardSymbolList;
        /**
         * 中奖线元素坐标列表
         */
        private List<SymbolIndexDo> rewardIndexList;

    }
}
