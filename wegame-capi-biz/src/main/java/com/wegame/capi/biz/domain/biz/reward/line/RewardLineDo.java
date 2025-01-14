package com.wegame.capi.biz.domain.biz.reward.line;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.common.enums.RewardLineSubTypeEnum;
import com.wegame.capi.common.enums.RewardLineTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
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
public class RewardLineDo implements Serializable {
    private static final long serialVersionUID = -2878827643634848L;
    /**
     * 中奖元素
     */
    private SymbolDo rewardSymbol;
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
    private int rewardLineCount = 0;
    //    /**
    //     * 中奖线的中奖元素列表
    //     */
    //    private List<SymbolDo> rewardSymbolList;
    //    /**
    //     * 中奖线的中奖元素坐标列表
    //     */
    //    private List<SymbolIndexDo> rewardSymbolIndexList;

    public boolean isCommonReward() {
        return Objects.equals(rewardLineSubType, RewardLineSubTypeEnum.COMMON);
    }

    public boolean isScatterReward() {
        return Objects.equals(rewardLineSubType, RewardLineSubTypeEnum.SCATTER);
    }
}
