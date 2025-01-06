package com.wegame.capi.biz.domain.biz.reward;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolIndexDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 中奖线do
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RewardLineDo implements Serializable {
    private static final long serialVersionUID = -2876565534848L;
    /**
     * 中奖元素
     */
    private SymbolDo symbol;
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
     * 中奖线的中奖元素坐标列表
     */
    private List<SymbolIndexDo> symbolIndexList;
    /**
     * 中奖线的中奖元素列表
     */
    private List<SymbolDo> symbolList;
}
