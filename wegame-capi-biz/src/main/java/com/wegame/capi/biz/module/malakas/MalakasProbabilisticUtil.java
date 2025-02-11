package com.wegame.capi.biz.module.malakas;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.SymbolSizeEnum;
import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MalakasProbabilisticUtil {


    public static Map<String, Integer> buildColumSymboleSizeProbabilisticMap(int currentColumNo,
                                                                      int maxColumSpan,
                                                                      MalakasConfig.ProbabilisticConfig probabilisticConfig) {
        Map<String, List<BigDecimal>> sizeProbabilisticConfigMap = probabilisticConfig.getSymbolSizeProbabilisticConfig();
        AssertUtil.assertTrue(MapUtils.isNotEmpty(sizeProbabilisticConfigMap), ErrorCode.ERR_INTER);
        Map<String, Integer> map = new HashMap<>();
        sizeProbabilisticConfigMap.entrySet().forEach(item -> {
            SymbolSizeEnum symbolSize = SymbolSizeEnum.valueOfCode(item.getKey());
            int val = (item.getValue().get(currentColumNo).multiply(new BigDecimal(Constants.CONST_10000))).intValue();
            if (val > 0 && maxColumSpan >= symbolSize.getColumSpan()) {
                map.put(item.getKey(), val);
            }
        });
        return map;
    }


    public static Map<String, List<BigDecimal>> getWheelProbabilisticConfig(GamePlayDo gamePlay, MalakasConfig.ProbabilisticConfig probabilisticConfig, boolean freePlay) {
        if (freePlay) {
            return probabilisticConfig.getFreePlayWheelProbabilisticConfig();
        } else {
            switch (gamePlay.getBetSubType()) {
                case NORMAL_BET:
                case BUY_BET:
                    return probabilisticConfig.getCommonPlayWheelProbabilisticConfig();
                case FREE_SPINS_NORMAL_BET:
                case FREE_SPINS_BUY_BET:
                    return probabilisticConfig.getFreePlayWheelProbabilisticConfig();
                default:
                    throw new BusinessException(ErrorCode.ERR_PARAM, "NOT SUPPORTED");
            }
        }
    }
}
