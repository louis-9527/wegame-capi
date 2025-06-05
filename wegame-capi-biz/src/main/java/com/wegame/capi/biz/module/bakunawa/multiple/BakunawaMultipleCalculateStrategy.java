package com.wegame.capi.biz.module.bakunawa.multiple;

import cn.hutool.core.util.NumberUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.BakunawaConfig;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.MultiplePieTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.utils.GameUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@Component("bakunawaMultipleCalculateStrategy")
public class BakunawaMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<SymbolDo> {

    @Autowired
    BakunawaConfig bakunawaConfig;
    @Autowired
    RandomService randomService;

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<SymbolDo> rewardHandle, List<RewardLineDo<SymbolDo>> rewardLineList) {
        if (CollectionUtils.isEmpty(rewardLineList) || CollectionUtils.isEmpty(rewardLineList.get(0).getRewardSymbolList())) {
            return BigDecimal.ZERO;
        }
        RewardLineDo<SymbolDo> rewardLineDo = rewardLineList.get(0);
        // 中奖元素id字符串拼接得到滚盘倍率
        String multipleStr = rewardLineDo.getRewardSymbolList()
                .stream()
                .filter(item -> !item.getSymbol().getId().equals(SymbolEnum.SymbolNull.getId()))
                .map(RewardSymbolDo::getSymbol)
                .map(SymbolDo::getId)
                .collect(Collectors.joining(""));
        AssertUtil.assertTrue(NumberUtil.isNumber(multipleStr), ErrorCode.ERR_PARAM, "multiples is error");
        return new BigDecimal(multipleStr);
    }

    @Override
    public Pair<MultiplePieTypeEnum, BigDecimal> calculateMultiplePie(RewardHandleDo<SymbolDo> rewardHandle) {
        // 传入当前倍率盘数据，则直接返回倍率盘数据，应用场景：dos todo @maidong
        //        if (Objects.nonNull(rewardHandle.getCurrentMultiple()) && rewardHandle.getCurrentMultiple().compareTo(BigDecimal.ZERO) > 0) {
        //            return rewardHandle.getCurrentMultiple();
        //        }
        TreeMap<String, BigDecimal> originalMultiplePieProbabilisticConfig = bakunawaConfig.getProbabilisticConfig().getMultiplePieProbabilisticConfig();
        TreeMap<String, Integer> multipleProbabilisticMap = new TreeMap<>();
        originalMultiplePieProbabilisticConfig.forEach((key, value) -> multipleProbabilisticMap.put(key, value.multiply(new BigDecimal(Constants.CONST_10000)).intValue()));
        // 获取基础倍率
        String multiple = GameUtil.randomKey(multipleProbabilisticMap, randomService.randomInt(1, Constants.CONST_1000000));
        if (!MultiplePieTypeEnum.BAKUNAWA.getCode().equals(multiple)) {
            return ImmutablePair.of(MultiplePieTypeEnum.NORMAL, new BigDecimal(multiple));
        } else {
            // 计算吞月倍率盘倍率
            TreeMap<String, BigDecimal> originalSuperMultiplePieProbabilisticConfig = bakunawaConfig.getProbabilisticConfig().getSuperMultiplePieProbabilisticConfig();
            TreeMap<String, Integer> superMultiPleProbabilisticMap = new TreeMap<>();
            originalSuperMultiplePieProbabilisticConfig.forEach((key, value) -> superMultiPleProbabilisticMap.put(key, value.multiply(new BigDecimal(Constants.CONST_10000)).intValue()));
            // 获取赔率盘倍率
            String finalMultiple = GameUtil.randomKey(superMultiPleProbabilisticMap, randomService.randomInt(1, Constants.CONST_1000000));
            return ImmutablePair.of(MultiplePieTypeEnum.BAKUNAWA, new BigDecimal(finalMultiple));
        }
    }
}
