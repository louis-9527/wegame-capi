package com.wegame.capi.biz.module.diawatamayari.multiple;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.biz.config.domain.DiwataMayariConfig;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component("diwataMayariMultipleCalculateStrategy")
public class DiwataMayariMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<DiwataMayariSymbolDo> {

    @Autowired
    private DiwataMayariConfig diwataMayariConfig;

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<DiwataMayariSymbolDo> rewardHandle, List<RewardLineDo<DiwataMayariSymbolDo>> rewardLineList) {
        // 区分正常轮和免费轮
        if (rewardHandle.getWheel().isFreeFlag()) {
            // 免费轮
            return this.calculateMultiple(rewardHandle.getCurrentMultiple(),
                    diwataMayariConfig.getMultiplePieRuleConfig().getFreePlayMinMultiple(),
                    rewardHandle.getWheel().getIndex(),
                    CollectionUtil.isNotEmpty(rewardLineList));
        } else {
            // 正常轮
            return this.calculateMultiple(rewardHandle.getCurrentMultiple(),
                    diwataMayariConfig.getMultiplePieRuleConfig().getCommonPlayMinMultiple(),
                    rewardHandle.getWheel().getIndex(),
                    CollectionUtil.isNotEmpty(rewardLineList));
        }
    }

    /**
     * 计算倍率盘倍率
     *
     * @param currentMultiple 当前倍率
     * @param minMultiple     最小倍率
     * @param isReward        是否中奖
     * @return 倍率盘倍率
     */
    private BigDecimal calculateMultiple(BigDecimal currentMultiple, BigDecimal minMultiple, Integer wheelIndex, boolean isReward) {
        // 当前倍率为空 or or 首轮->最小倍率
        if (Objects.isNull(currentMultiple) || currentMultiple.compareTo(BigDecimal.ZERO) <= 0 || wheelIndex == 0) {
            return minMultiple;
        }
        // 如果没有中奖，返回当前倍率
        if (!isReward) {
            return currentMultiple;
        } else {
            // 当前计算后的倍率大于最大倍率，返回最大.否则返回当前倍率*2
            BigDecimal maxMultiple = diwataMayariConfig.getMultiplePieRuleConfig().getMaxMultiple();
            BigDecimal calcMultiple = currentMultiple.multiply(BigDecimal.valueOf(2));
            return calcMultiple.compareTo(maxMultiple) >= 0 ? maxMultiple : calcMultiple;
        }

    }
}
