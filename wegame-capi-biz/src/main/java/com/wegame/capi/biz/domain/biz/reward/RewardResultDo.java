package com.wegame.capi.biz.domain.biz.reward;

import com.wegame.capi.common.enums.RewardTypeEnum;
import lombok.AllArgsConstructor;
import com.wegame.capi.common.enums.RewardSubTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 中奖结果do
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RewardResultDo implements Serializable {
    private static final long serialVersionUID = -2262432373454848L;
    /**
     * 中奖类型
     */
    private RewardTypeEnum rewardType;
    /**
     * 中奖子类型
     */
    private RewardSubTypeEnum rewardSubType;
    /**
     * 投注金额
     */
    private Long betAmount;
    /**
     * 中奖总金额.单位：分
     */
    private Long totalRewardAmount;
    /**
     * 是否中了免费轮
     */
    private boolean hasRewardFreeSpin;
    /**
     * 中奖线列表
     */
    private List<RewardLineDetailDo> rewardLineDetailList;
    /**
     * 中奖总倍率
     */
    private BigDecimal totalMultiple;
}
