package com.wegame.capi.biz.module.bakunawa.multiple;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.BakunawaConfig;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.IbongAdarnaSymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.MultipleTypeEnum;
import com.wegame.capi.common.utils.GameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

@Slf4j
@Component("bakunawaMultipleCalculateStrategy")
public class BakunawaMultipleCalculateStrategy
        extends AbstractMultipleCalculateStrategy<IbongAdarnaSymbolDo> {
    @Autowired // todo @maidong
    BakunawaConfig bakunawaConfig;
    @Autowired
    RandomService randomService;

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<IbongAdarnaSymbolDo> rewardHandle, List<RewardLineDo<IbongAdarnaSymbolDo>> rewardLineList) {
        // 传入当前倍率盘数据，则直接返回倍率盘数据，应用场景：dos
        if (Objects.nonNull(rewardHandle.getCurrentMultiple()) && rewardHandle.getCurrentMultiple().compareTo(BigDecimal.ZERO) > 0) {
            return rewardHandle.getCurrentMultiple();
        }
        BetSubTypeEnum betSubType = rewardHandle.getBetSubType();
        AssertUtil.assertNotNull(betSubType, ErrorCode.ERR_INTER, "betSubType NOT SUPPORTED");
        return this.randomMultiple(betSubType);
    }

    private BigDecimal randomMultiple(BetSubTypeEnum betSubType) {
        // 取出对应投注类型的倍率出现概率配置
        TreeMap<BigDecimal, TreeMap<String, BigDecimal>> multiPleProbabilisticConfig = null; // todo @maidong
        // bakunawaConfig.getProbabilisticConfig().getMultiplierWheelProbabilisticConfig();
        TreeMap<BigDecimal, Integer> betTypeMultiPleProbabilisticMap = new TreeMap<>();
        MultipleTypeEnum multipleType = MultipleTypeEnum.fromBetSubTypeEnum(betSubType);
        multiPleProbabilisticConfig.forEach((key, value) -> {
            betTypeMultiPleProbabilisticMap.put(key, value.get(multipleType.getCode()).multiply(new BigDecimal(Constants.CONST_10000)).intValue());
        });
        // 随机取出一个倍率
        return GameUtil.randomKey(betTypeMultiPleProbabilisticMap, randomService.randomInt(1, Constants.CONST_1000000));
    }
}
