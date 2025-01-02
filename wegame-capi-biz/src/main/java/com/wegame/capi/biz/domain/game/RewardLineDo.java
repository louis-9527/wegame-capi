package com.wegame.capi.biz.domain.game;

import com.wegame.capi.biz.domain.SymbolDo;
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
     * 元素id
     */
    private SymbolDo symbolDo;
    /**
     * 中奖金额
     */
    private Long rewardAmount;
//    /**
//     * 元素连数
//     */
//    private Integer linkCount;
    /**
     * 中奖倍数
     */
    private BigDecimal rewardMultiple;
    /**
     * 赔率
     */
    private BigDecimal odds;
    /**
     * 当前中奖线的中奖id列表
     */
    private List<SymbolDo> rewardSymbolList;
}
