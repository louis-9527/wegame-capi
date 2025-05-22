package com.wegame.capi.biz.module.lamang;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.SymbolSizeEnum;
import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LamangProbabilisticUtil {


    public static LamangConfig.PlayProbabilisticConfig getSuperWildProbabilisticConfig(GamePlayDo gamePlay,LamangConfig.ProbabilisticConfig probabilisticConfig, boolean freePlay, boolean superWild) {
        if (superWild) {
            return probabilisticConfig.getSupperWildRefreshProbabilisticConfig();
        } else if (freePlay) {
            return probabilisticConfig.getFreePlaySymbolProbabilisticConfig();
        }
        else {
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
}
