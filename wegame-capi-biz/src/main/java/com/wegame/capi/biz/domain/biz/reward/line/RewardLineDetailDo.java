package com.wegame.capi.biz.domain.biz.reward.line;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolIndexDo;
import com.wegame.capi.common.enums.RewardLineSubTypeEnum;
import com.wegame.capi.common.enums.RewardLineTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 中奖线明细
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RewardLineDetailDo implements Serializable {
    private static final long serialVersionUID = -2876565534848L;
    /**
     * 投注金额
     */
    private Long betAmount;
    /**
     * 中奖金额
     */
    private Long rewardAmount;
    /**
     * 赔率
     */
    private BigDecimal odds;
    /**
     * 倍率
     */
    private BigDecimal multiple;
    /**
     * 中奖元素
     */
    private SymbolDo symbol;
    /**
     * 中奖线类型
     */
    private RewardLineTypeEnum rewardLineType;
    /**
     * 中奖线子类型
     */
    private RewardLineSubTypeEnum rewardLineSubType;
    /**
     * 中奖线数量
     */
    private Integer rewardLineCount;

    //    /**
    //     * 中奖线的中奖元素列表
    //     */
    //    private List<SymbolDo> symbolList;
    //    /**
    //     * 中奖线的中奖元素坐标列表
    //     */
    //    private List<SymbolIndexDo> symbolIndexList;

    public void print() {
        System.out.println("投注金额：" + betAmount);
        System.out.println("中奖金额：" + rewardAmount);
        System.out.println("中奖赔率：" + odds);
        System.out.println("中奖倍数：" + multiple);
        System.out.println("中奖元素：" + (Objects.nonNull(symbol) ? symbol.getName() : ""));
        System.out.println("中奖线类型：" + (Objects.nonNull(rewardLineType) ? rewardLineType.getDesc() : ""));
        System.out.println("中奖线子类型：" + (Objects.nonNull(rewardLineSubType) ? rewardLineSubType.getDesc() : ""));
        System.out.println("中奖线数量：" + rewardLineCount);
    }
}
