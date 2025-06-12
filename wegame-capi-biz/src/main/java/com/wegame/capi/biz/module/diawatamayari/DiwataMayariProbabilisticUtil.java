package com.wegame.capi.biz.module.diawatamayari;

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

public class DiwataMayariProbabilisticUtil {

    public static Map<String, Integer> buildColumSymboleSizeProbabilisticMap(int currentColumnNo,
                                                                             int maxColumnSpan,
                                                                             MalakasConfig.PlayProbabilisticConfig playProbabilisticConfig) {
        Map<String, List<BigDecimal>> sizeProbabilisticConfigMap = playProbabilisticConfig.getSymbolSizeProbabilisticConfig();
        AssertUtil.assertTrue(MapUtils.isNotEmpty(sizeProbabilisticConfigMap), ErrorCode.ERR_INTER);
        Map<String, Integer> map = new HashMap<>();
        sizeProbabilisticConfigMap.forEach((key, value) -> {
            SymbolSizeEnum symbolSize = SymbolSizeEnum.valueOfCode(key);
            int val = (value.get(currentColumnNo).multiply(new BigDecimal(Constants.CONST_10000))).intValue();
            if (val > 0 && maxColumnSpan >= symbolSize.getRowSpan()) {
                map.put(key, val);
            }
        });
        return map;
    }

    public static MalakasConfig.PlayProbabilisticConfig getWheelProbabilisticConfig(GamePlayDo gamePlay, MalakasConfig.ProbabilisticConfig probabilisticConfig, boolean freePlay) {
        if (freePlay) {
            return probabilisticConfig.getFreePlayProbabilisticConfig();
        } else {
            switch (gamePlay.getBetSubType()) {
                case NORMAL_BET:
                case BUY_BET:
                    return probabilisticConfig.getCommonPlayProbabilisticConfig();
                case FREE_SPINS_NORMAL_BET:
                case FREE_SPINS_BUY_BET:
                    return probabilisticConfig.getFreePlayProbabilisticConfig();
                default:
                    throw new BusinessException(ErrorCode.ERR_PARAM, "NOT SUPPORTED");
            }
        }
    }
}
