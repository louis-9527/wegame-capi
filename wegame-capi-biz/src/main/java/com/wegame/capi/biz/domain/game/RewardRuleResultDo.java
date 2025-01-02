package com.wegame.capi.biz.domain.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 中奖规则结果do
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RewardRuleResultDo implements Serializable {
    private static final long serialVersionUID = -2266308273454848L;
    /**
     * 中奖线中奖列表
     */
    private List<RewardRuleLineDo> rewardRuleLineList;

    /**
     * 所有命中规则的元素坐标集合（不重复）
     */
    private Set<SymbolIndexDo> rewardIndexList;


}
