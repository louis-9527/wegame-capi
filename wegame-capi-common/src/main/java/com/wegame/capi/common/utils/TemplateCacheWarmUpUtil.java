package com.wegame.capi.common.utils;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.BetTypeTemplateOddsWarmUpEnum;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class TemplateCacheWarmUpUtil {


    public static Boolean isWarmUpMultipleRange(BetTypeEnum betType, TemplateMultipleRange multipleRange) {
        AssertUtil.assertNotNull(betType, ErrorCode.ERR_PARAM);
        BetTypeTemplateOddsWarmUpEnum templateOddsWarmUpEnum = BetTypeTemplateOddsWarmUpEnum.valueOfCode(betType.getCode());
        AssertUtil.assertNotNull(templateOddsWarmUpEnum, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(multipleRange, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(multipleRange.getStart(), ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(multipleRange.getEnd(), ErrorCode.ERR_PARAM);

        Boolean normalBetFlag = templateOddsWarmUpEnum == BetTypeTemplateOddsWarmUpEnum.NORMAL_BET && multipleRange.getEnd().compareTo(new BigDecimal(templateOddsWarmUpEnum.getOdds())) <= 0;
        Boolean buyBetFlag = templateOddsWarmUpEnum == BetTypeTemplateOddsWarmUpEnum.BUY_BET && multipleRange.getEnd().compareTo(new BigDecimal(templateOddsWarmUpEnum.getOdds())) <= 0;
        return normalBetFlag || buyBetFlag;
    }
}
