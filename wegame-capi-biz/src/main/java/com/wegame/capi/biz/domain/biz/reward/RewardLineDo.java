package com.wegame.capi.biz.domain.biz.reward;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolIndexDo;
import com.wegame.capi.common.enums.RewardLineSubTypeEnum;
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
     * 中奖线的中奖元素列表
     */
    private List<SymbolDo> rewardSymbolList;
    /**
     * 中奖线的中奖元素坐标列表
     */
    private List<SymbolIndexDo> rewardSymbolIndexList;

    public boolean isCommonReward() {
        return Objects.equals(rewardSymbol.getSymbolType(), SymbolTypeEnum.COMMON);
    }
}
