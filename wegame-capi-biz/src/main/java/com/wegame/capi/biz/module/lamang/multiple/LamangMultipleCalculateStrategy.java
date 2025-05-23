package com.wegame.capi.biz.module.lamang.multiple;

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
        BigDecimal currentMultiple = Objects.isNull(rewardHandle.getCurrentMultiple()) ? BigDecimal.ZERO : rewardHandle.getCurrentMultiple();
        BigDecimal multipleWheelMultiple = this.calculateMultipleWheelMultiple(rewardHandle, rewardLineList);
        // 当前累计倍率+本次倍率盘倍率
        return currentMultiple.add(multipleWheelMultiple);
    }

    @Override
    public <R> R calculateMultipleWheelMultiple(RewardHandleDo<LamangSymbolDo> rewardHandle, List<RewardLineDo<LamangSymbolDo>> rewardLineList) {
        // 第一回合返回1
        if (rewardHandle.getWheel().getIndex() == 0) {
            return (R) BigDecimal.ONE;
        }
        // 未中奖返回上一回合的倍率
        if (CollectionUtils.isEmpty(rewardLineList)) {
            // 未消除保留原有倍率
            return (R) rewardHandle.getCurrentRoundMultiple();
        }
        if (rewardHandle.getWheel().isFreeFlag()) {
            // 免费轮
            return (R) calculateMultipleWheelMultiple(lamangConfig.getMultiPleConfig().getFreePlayMultiConfigList(), rewardHandle.getCurrentRoundMultiple());
        } else {
            // 普通轮
            return (R) calculateMultipleWheelMultiple(lamangConfig.getMultiPleConfig().getCommonPlayMultiConfigList(), rewardHandle.getCurrentRoundMultiple());
        }
    }

    public BigDecimal calculateMultipleWheelMultiple(List<BigDecimal> playMultiConfigList, BigDecimal currentRoundMultiple) {
        int maxIndex = playMultiConfigList.size() - 1;
        // 如果currentMultiple为null,则currentMultipleIndex会返回-1
        int currentMultipleIndex = playMultiConfigList.indexOf(currentRoundMultiple);
        return currentMultipleIndex >= maxIndex ? currentRoundMultiple : playMultiConfigList.get(currentMultipleIndex + 1);
    }
}
