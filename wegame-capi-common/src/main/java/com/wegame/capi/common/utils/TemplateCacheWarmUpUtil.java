package com.wegame.capi.common.utils;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.GameTypeTemplateOddsWarmUpEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class TemplateCacheWarmUpUtil {


    public static Boolean isWarmUpMultipleRange(GameTypeEnum gameType, BetTypeEnum betType, TemplateMultipleRange multipleRange) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betType, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(multipleRange, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(multipleRange.getStart(), ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(multipleRange.getEnd(), ErrorCode.ERR_PARAM);

        GameTypeTemplateOddsWarmUpEnum gameTypeOddsWarmUpEnum = GameTypeTemplateOddsWarmUpEnum.valueOfCode(gameType.getId());
        AssertUtil.assertNotNull(gameTypeOddsWarmUpEnum, ErrorCode.ERR_PARAM);

        Boolean normalBetFlag = betType == BetTypeEnum.NORMAL_BET && multipleRange.getEnd().compareTo(new BigDecimal(gameTypeOddsWarmUpEnum.getNormalBetOdds())) <= 0;
        Boolean buyBetFlag = betType == BetTypeEnum.BUY_BET && multipleRange.getEnd().compareTo(new BigDecimal(gameTypeOddsWarmUpEnum.getBuyBetOdds())) <= 0;
        return normalBetFlag || buyBetFlag;
    }
}
