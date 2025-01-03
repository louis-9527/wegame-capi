package com.wegame.capi.biz.domain.business;

import com.wegame.capi.common.enums.RewardLineTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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
     * 中奖线类型
     */
    private RewardLineTypeEnum rewardLineType;
    /**
     * 当前中奖线的中奖元素坐标列表
     */
    private List<SymbolIndexDo> rewardSymbolIndexList;
    /**
     * 当前中奖线的中奖元素列表
     */
    private List<SymbolDo> rewardSymbolIdList;


    public boolean isCommonReward() {
        return Objects.equals(this.symbol.getSymbolType(), SymbolTypeEnum.COMMON);
    }
}
