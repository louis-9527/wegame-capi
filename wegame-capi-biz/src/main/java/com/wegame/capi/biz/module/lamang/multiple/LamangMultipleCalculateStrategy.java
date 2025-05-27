package com.wegame.capi.biz.module.lamang.multiple;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.LamangSymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component("lamangMultipleCalculateStrategy")
public class LamangMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<LamangSymbolDo> {
    @Autowired
    LamangConfig lamangConfig;

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<LamangSymbolDo> rewardHandle, List<RewardLineDo<LamangSymbolDo>> rewardLineList) {
        boolean isReward = CollectionUtil.isNotEmpty(rewardLineList);
        BigDecimal currentMultiple = rewardHandle.getCurrentMultiple();
        if (rewardHandle.getWheel().isFreeFlag()) {
            // 免费轮
            return calculateMultipleWheelMultiple(lamangConfig.getMultiPleConfig().getFreePlayMultiConfigList(), currentMultiple, isReward);
        } else {
            // 普通轮
            return calculateMultipleWheelMultiple(lamangConfig.getMultiPleConfig().getCommonPlayMultiConfigList(), currentMultiple, isReward);
        }
    }

    private BigDecimal calculateMultipleWheelMultiple(List<BigDecimal> playMultiConfigList, BigDecimal currentMultiple, boolean isReward) {
        // 当前回合无倍率(上一回合(第一回合))，使用最小倍率
        if (Objects.isNull(currentMultiple)) {
            return playMultiConfigList.get(0);
        }
        // 未中奖，使用前一回合的倍率
        if (!isReward) {
            return currentMultiple;
        }
        int maxIndex = playMultiConfigList.size() - 1;
        int currentMultipleIndex = playMultiConfigList.indexOf(currentMultiple);
        // 兜一下,记录
        if (currentMultipleIndex == -1) {
            log.warn("LamangMultipleCalculateStrategy#calculateMultipleWheelMultiple" +
                    ",no match Multiple config Index,will return min index value,currentMultiple:{}", currentMultiple);
        }
        // 如果currentMultipleIndex为配置最大值,则取最大
        return currentMultipleIndex >= maxIndex ? playMultiConfigList.get(maxIndex) : playMultiConfigList.get(currentMultipleIndex + 1);
    }
}
