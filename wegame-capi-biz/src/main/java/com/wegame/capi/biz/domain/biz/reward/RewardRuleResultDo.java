package com.wegame.capi.biz.domain.biz.reward;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

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
}
