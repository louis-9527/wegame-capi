package com.wegame.capi.biz.module.lamang;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.common.Constants;
import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.TreeMap;

public class LamangProbabilisticUtil {

    public static LamangConfig.PlayProbabilisticConfig getSuperWildProbabilisticConfig(GamePlayDo gamePlay, LamangConfig.ProbabilisticConfig probabilisticConfig, boolean freePlay, boolean superWild) {
        if (superWild) {
            return probabilisticConfig.getSupperWildRefreshProbabilisticConfig();
        } else if (freePlay) {
            return probabilisticConfig.getFreePlaySymbolProbabilisticConfig();
        } else {
            switch (gamePlay.getBetSubType()) {
                case NORMAL_BET:
                case BUY_BET:
                    return probabilisticConfig.getCommonPlaySymbolProbabilisticConfig();
                case FREE_SPINS_NORMAL_BET:
                case FREE_SPINS_BUY_BET:
                    return probabilisticConfig.getFreePlaySymbolProbabilisticConfig();
                default:
                    throw new BusinessException(ErrorCode.ERR_PARAM, "NOT SUPPORTED");
            }
        }
    }


    public static TreeMap<String, Integer> buildWildProbabilisticMap(TreeMap<String, BigDecimal> probabilisticConfigMap) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(probabilisticConfigMap), ErrorCode.ERR_INTER);
        TreeMap<String, Integer> map = new TreeMap<>();
        probabilisticConfigMap.forEach((key, value) -> {
            int val = (value.multiply(new BigDecimal(Constants.CONST_10000))).intValue();
            if (val > 0) {
                map.put(key, val);
            }
        });
        Integer sum = map.values()
                .stream()
                .reduce(0, Integer::sum);
        AssertUtil.assertTrue(sum == Constants.CONST_1000000, ErrorCode.ERR_INTER, "wild Symbol config error:");
        return map;
    }
}
