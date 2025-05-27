package com.wegame.capi.biz.module.lamang.multiple;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.LamangSymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
        int currentRoundIndex;
        if (CollectionUtil.isEmpty(rewardLineList)) {
            if (rewardHandle.getWheel().getIndex() <= 0) {
                currentRoundIndex = 0;
            } else {
                currentRoundIndex = rewardHandle.getWheel().getIndex() - 1;
            }
        } else {
            currentRoundIndex = rewardHandle.getWheel().getIndex();
        }

        if (rewardHandle.getWheel().isFreeFlag()) {
            // 免费轮
            return calculateMultipleWheelMultiple(lamangConfig.getMultiPleConfig().getFreePlayMultiConfigList(), currentRoundIndex);
        } else {
            // 普通轮
            return calculateMultipleWheelMultiple(lamangConfig.getMultiPleConfig().getCommonPlayMultiConfigList(), currentRoundIndex);
        }
    }

    public BigDecimal calculateMultipleWheelMultiple(List<BigDecimal> playMultiConfigList, int currentRoundIndex) {
        int maxIndex = playMultiConfigList.size() - 1;
        // 如果currentRoundIndex为配置最大值,则去最大
        return currentRoundIndex >= maxIndex ? playMultiConfigList.get(maxIndex) : playMultiConfigList.get(currentRoundIndex);
    }
}
