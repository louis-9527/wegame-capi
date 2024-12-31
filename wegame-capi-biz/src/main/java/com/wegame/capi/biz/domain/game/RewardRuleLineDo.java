package com.wegame.capi.biz.domain.game;

import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.common.enums.RewardLineTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 中奖规则线do
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RewardRuleLineDo implements Serializable {
    private static final long serialVersionUID = -2878827643634848L;

    /**
     * 元素id
     */
    private SymbolDo symbol;
    /**
     * 元素连数
     */
    private RewardLineTypeEnum rewardLineType;
    /**
     * 中奖元素的坐标
     */
    private int[] rewardCoordinate;
    /**
     * 当前中奖线的中奖id列表
     */
    private List<Integer> rewardSymbolIdList;
}
