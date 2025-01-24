package com.wegame.capi.biz.domain.biz.wheel;

import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 滚轮中奖结果
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class WheelRewardDo<T extends SymbolDo> implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;

    /**
     * 滚轮中奖结果
     */
    private RewardResultDo<T> rewardResult;
}

