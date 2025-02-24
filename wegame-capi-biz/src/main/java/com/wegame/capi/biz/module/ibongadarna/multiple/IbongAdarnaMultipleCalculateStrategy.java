package com.wegame.capi.biz.module.ibongadarna.multiple;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.IbongSymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.MultipleTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("ibongAdarnaMultipleCalculateStrategy")
public class IbongAdarnaMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<IbongSymbolDo> {
    @Autowired
    IbongAdarnaConfig ibongAdarnaConfig;
    @Autowired
    RandomService randomService;


    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<IbongSymbolDo> rewardHandle, List<RewardLineDo<IbongSymbolDo>> rewardLineList) {
        BetSubTypeEnum betSubType = rewardHandle.getBetSubType();
        AssertUtil.assertNotNull(betSubType, ErrorCode.ERR_INTER, "betSubType NOT SUPPORTED");
        return this.randomMultiple(betSubType);
    }

    private BigDecimal randomMultiple(BetSubTypeEnum betSubType) {
        // 取出对应投注类型的倍率出现概率配置
        Map<BigDecimal, Map<String, BigDecimal>> multiPleProbabilisticConfig = ibongAdarnaConfig.getProbabilisticConfig().getMultiPleProbabilisticConfig();
        Map<BigDecimal, Integer> betTypemultiPleProbabilisticMap = new HashMap<>();
        MultipleTypeEnum multipleType = MultipleTypeEnum.fromBetSubTypeEnum(betSubType);
        multiPleProbabilisticConfig.forEach((key, value) -> {
            betTypemultiPleProbabilisticMap.put(key, value.get(multipleType.getCode()).multiply(new BigDecimal(Constants.CONST_10000)).intValue());
        });

        // 随机取出一个倍率
        int random = randomService.randomInt(1, Constants.CONST_1000000);
        int startVal = 0;
        for (Map.Entry<BigDecimal, Integer> entry : CollectionUtils.emptyIfNull(betTypemultiPleProbabilisticMap.entrySet())) {
            if (random > startVal && random <= startVal + entry.getValue()) {
                return entry.getKey();
            } else {
                startVal += entry.getValue();
            }
        }
        throw new BusinessException(ErrorCode.ERR_INTER, "no suport multiple probabilistic");
    }
}
