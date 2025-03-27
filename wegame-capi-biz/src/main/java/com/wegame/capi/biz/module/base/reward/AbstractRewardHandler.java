package com.wegame.capi.biz.module.base.reward;

import cn.hutool.core.util.NumberUtil;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.common.enums.RewardSubTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 抽象中奖策略
 */
@Slf4j
@Service
public abstract class AbstractRewardHandler<T extends SymbolDo>
        implements RewardHandler<T> {

    /**
     * 计算中奖线中奖金额
     *
     * @param rewardLine 中奖线
     * @param betAmount  投注金额
     * @param odds       赔率
     * @param multiple   倍率
     * @return 中奖金额
     */
    public BigDecimal calculateLineRewardAmount(RewardLineDo rewardLine, Long betAmount, BigDecimal odds, BigDecimal multiple) {
        if (rewardLine.isCommonReward()) {
            return BigDecimal.valueOf(betAmount)
                    .multiply(odds)
                    .multiply(new BigDecimal("" + rewardLine.getRewardLineCount()))
                    .multiply(multiple);
        } else if (rewardLine.isScatterReward()) {
            // scatter 无需进行倍率计算
            return BigDecimal.valueOf(betAmount)
                    .multiply(odds)
                    .multiply(new BigDecimal("" + rewardLine.getRewardLineCount()));
        } else {
            return BigDecimal.valueOf(betAmount)
                    .multiply(multiple)
                    .multiply(new BigDecimal("" + rewardLine.getRewardLineCount()));
        }
    }

    /**
     * 计算中奖子类型
     * @param betAmount 投注金额
     * @param totalRewardAmount 中奖总金额
     * @return 中奖子类型
     */
    public RewardSubTypeEnum calculateRewardSubType(Long betAmount, Long totalRewardAmount) {
        if (Objects.isNull(betAmount)
                || Objects.isNull(totalRewardAmount)
                || betAmount <= 0
                || totalRewardAmount <= 0) {
            return RewardSubTypeEnum.NONE;
        }
        double rewardTimes = NumberUtil.div(totalRewardAmount.longValue(), betAmount.longValue());
        if (rewardTimes >= 5 && rewardTimes < 10) {
            return RewardSubTypeEnum.NICE;
        } else if (rewardTimes >= 10 && rewardTimes < 15) {
            return RewardSubTypeEnum.MEGA;
        } else if (rewardTimes >= 15 && rewardTimes < 20) {
            return RewardSubTypeEnum.SUPER;
        } else if (rewardTimes >= 20) {
            return RewardSubTypeEnum.SENSATIONAL;
        } else {
            return RewardSubTypeEnum.OTHER;
        }
    }

    public boolean hasRewardScatter(List<RewardLineDo<T>> rewardLineList) {
        return CollectionUtils.isNotEmpty(rewardLineList) && rewardLineList.stream().anyMatch(r -> r.isScatterReward());
    }
}
