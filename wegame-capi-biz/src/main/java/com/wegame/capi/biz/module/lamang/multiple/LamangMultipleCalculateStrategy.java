package com.wegame.capi.biz.module.lamang.multiple;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.LamangSymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.wegame.capi.common.Constants.LAMANG_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Component("lamangMultipleCalculateStrategy")
@ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
public class LamangMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<LamangSymbolDo> {
    @Autowired
    LamangConfig lamangConfig;

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<LamangSymbolDo> rewardHandle, List<RewardLineDo<LamangSymbolDo>> rewardLineList) {
        boolean isReward = CollectionUtil.isNotEmpty(rewardLineList);
        BigDecimal currentMultiple = rewardHandle.getCurrentMultiple();
        Integer currentWheelIndex = rewardHandle.getWheel().getIndex();
        if (rewardHandle.getWheel().isFreeFlag()) {
            // 免费轮
            return calculateMultipleWheelMultiple(lamangConfig.getMultiPleConfig().getFreePlayMultiConfigList(), currentMultiple, currentWheelIndex, isReward);
        } else {
            // 普通轮
            return calculateMultipleWheelMultiple(lamangConfig.getMultiPleConfig().getCommonPlayMultiConfigList(), currentMultiple, currentWheelIndex, isReward);
        }
    }

    private BigDecimal calculateMultipleWheelMultiple(List<BigDecimal> playMultiConfigList, BigDecimal currentMultiple, Integer currentWheelIndex, boolean isReward) {
        // 当前回合无倍率(上一回合(第一回合))，使用最小倍率
        if (Objects.isNull(currentMultiple) || Objects.isNull(currentWheelIndex) || currentWheelIndex <= 0) {
            return playMultiConfigList.get(0);
        }
        // 未中奖，使用前一回合的倍率
        if (!isReward) {
            return currentMultiple;
        }
        // 当前回合数和最大倍率配置索引比较，取小
        // 如果currentMultipleIndex为配置最大值,则取最大
        return playMultiConfigList.get(Math.min(playMultiConfigList.size() - 1, currentWheelIndex));
    }
}
