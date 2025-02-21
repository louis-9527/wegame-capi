package com.wegame.capi.biz.domain.biz.reward.line;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.common.enums.RewardLineSubTypeEnum;
import com.wegame.capi.common.enums.RewardLineTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 中奖线
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RewardLineDo<T extends SymbolDo> implements Serializable {
    private static final long serialVersionUID = -2878827643634848L;
    /**
     * 中奖线类型
     */
    private RewardLineTypeEnum rewardLineType;
    /**
     * 中奖线子类型
     */
    private RewardLineSubTypeEnum rewardLineSubType;

    /**
     * 中奖线号
     */
    private Integer rewardLineNo;

    /**
     * 中奖线数量
     */
    private int rewardLineCount;
    /**
     * 中奖元素
     */
    private T rewardSymbol;
    /**
     * 中奖线的中奖元素列表
     */
    private List<RewardSymbolDo<T>> rewardSymbolList;

    public boolean isCommonReward() {
        return Objects.equals(rewardLineSubType, RewardLineSubTypeEnum.COMMON);
    }

    public boolean isScatterReward() {
        return Objects.equals(rewardLineSubType, RewardLineSubTypeEnum.SCATTER);
    }
}
