package com.wegame.capi.biz.module.base.reward;

import cn.hutool.core.util.NumberUtil;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.common.enums.RewardSubTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 抽象中奖策略
 */
@Slf4j
@Service
public abstract class AbstractRewardHandler<T extends SymbolDo> implements RewardHandler<T> {

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

    public boolean hasRewardScatter(List<RewardLineDo<T>> rewardLineList) {
        return CollectionUtils.isNotEmpty(rewardLineList) && rewardLineList.stream().anyMatch(r -> r.isScatterReward());
    }

    public Integer scatterCount(RewardHandleDo<T> rewardHandle) {
        WheelDo<T> wheel = rewardHandle.getWheel();
        long count = Arrays.stream(wheel.getSymbolArr()).flatMap(Arrays::stream).filter(Objects::nonNull).filter(SymbolDo::isScatterSymbol).count();
        return (int) count;
    }

}
