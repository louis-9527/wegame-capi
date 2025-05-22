package com.wegame.capi.biz.module.bakunawa.multiple;

import com.wegame.capi.biz.config.domain.BakunawaConfig;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.MultiplePieTypeEnum;
import com.wegame.capi.common.utils.GameUtil;
import javafx.util.Pair;
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
        extends AbstractMultipleCalculateStrategy<SymbolDo> {

    private static final String SPECIAL_MULTIPLE = "Bakunawa";

    @Autowired
    BakunawaConfig bakunawaConfig;
    @Autowired
    RandomService randomService;

    // 计算基础倍率： todo @maidong 根据一位数组中 【】 【】 【】
    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<SymbolDo> rewardHandle, List<RewardLineDo<SymbolDo>> rewardLineList) {
        // 传入当前倍率盘数据，则直接返回倍率盘数据，应用场景：dos
        if (Objects.nonNull(rewardHandle.getCurrentMultiple()) && rewardHandle.getCurrentMultiple().compareTo(BigDecimal.ZERO) > 0) {
            return rewardHandle.getCurrentMultiple();
        }
        return randomMultiple();
    }

    private BigDecimal randomMultiple() {
        // 取出对应投注类型的倍率出现概率配置
        TreeMap<String, BigDecimal> multiPleProbabilisticConfig = bakunawaConfig.getProbabilisticConfig().getMultiplierWheelProbabilisticConfig();
        TreeMap<BigDecimal, Integer> betTypeMultiPleProbabilisticMap = new TreeMap<>();
        multiPleProbabilisticConfig.forEach((key, value) -> {
            betTypeMultiPleProbabilisticMap.put(new BigDecimal(key), value.multiply(new BigDecimal(Constants.CONST_10000)).intValue());
        });
        // 随机取出一个倍率
        return GameUtil.randomKey(betTypeMultiPleProbabilisticMap, randomService.randomInt(1, Constants.CONST_1000000));
    }


    @Override
    public Pair<MultiplePieTypeEnum, BigDecimal> calculateMultiplePie(RewardHandleDo<SymbolDo> rewardHandle) {
        // 传入当前倍率盘数据，则直接返回倍率盘数据，应用场景：dos todo @maidong
        //        if (Objects.nonNull(rewardHandle.getCurrentMultiple()) && rewardHandle.getCurrentMultiple().compareTo(BigDecimal.ZERO) > 0) {
        //            return rewardHandle.getCurrentMultiple();
        //        }
        TreeMap<String, BigDecimal> originalMultiplePieProbabilisticConfig = bakunawaConfig.getProbabilisticConfig().getMultiplePieProbabilisticConfig();
        TreeMap<String, Integer> multipleProbabilisticMap = new TreeMap<>();
        originalMultiplePieProbabilisticConfig.forEach((key, value) -> {
            multipleProbabilisticMap.put(key, value.multiply(new BigDecimal(Constants.CONST_10000)).intValue());
        });
        // 随机取出一个倍率
        String multiple = GameUtil.randomKey(multipleProbabilisticMap, randomService.randomInt(1, Constants.CONST_1000000));
        if (!SPECIAL_MULTIPLE.equals(multiple)) {
            return new Pair<>(MultiplePieTypeEnum.NORMAL, new BigDecimal(multiple));
        } else {
            // 计算吞月倍率盘倍率
            TreeMap<String, BigDecimal> originalSuperMultiplePieProbabilisticConfig = bakunawaConfig.getProbabilisticConfig().getMultiplePieProbabilisticConfig();
            TreeMap<String, Integer> superMultiPleProbabilisticMap = new TreeMap<>();
            originalSuperMultiplePieProbabilisticConfig.forEach((key, value) -> {
                superMultiPleProbabilisticMap.put(key, value.multiply(new BigDecimal(Constants.CONST_10000)).intValue());
            });
            // 随机取出一个倍率
            String finalMultiple = GameUtil.randomKey(superMultiPleProbabilisticMap, randomService.randomInt(1, Constants.CONST_1000000));
            return new Pair<>(MultiplePieTypeEnum.BAKUNAWA, new BigDecimal(finalMultiple));
        }
    }
}
