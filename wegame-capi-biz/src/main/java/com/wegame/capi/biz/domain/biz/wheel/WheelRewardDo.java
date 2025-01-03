package com.wegame.capi.biz.domain.biz.wheel;

import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 滚轮
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class WheelRewardDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 滚轮序号: 默认1
     */
    private Integer wheelIndex = 1;
    /**
     * 滚轮
     */
    private WheelDo wheel;
    /**
     * 中奖结果（滚轮对应中奖结果）
     */
    private RewardResultDo rewardResult;
}

