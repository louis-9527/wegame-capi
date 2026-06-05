package com.wegame.capi.biz.builder;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.common.UserMetricHelper;

import java.util.Map;

public class UserMetricInfoBuilder {

    public static UserMetricInfoDo build(Map<String, Object> map) {
        AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
        return UserMetricInfoDo.builder()
                .totalBetTimes(UserMetricHelper.getTotalBetTimes(map))
                .todayBetTimes(UserMetricHelper.getTodayBetTimes(map))
                .totalBetAmount(UserMetricHelper.getTotalBetAmount(map))
                .totalPayoutAmount(UserMetricHelper.getTotalPayoutAmount(map))
                .continueEmptyTimes(UserMetricHelper.getContinueEmptyTimes(map))
                .todayContinueNoFgTimes(UserMetricHelper.getContinueNoFgTimes(map))
                .enterBalance(UserMetricHelper.getEnterBalance(map))
                .exitBalance(UserMetricHelper.getExitBalance(map))
                .currentBalance(UserMetricHelper.getCurrentBalance(map))
                .registerTime(UserMetricHelper.getRegisterTime(map))
                .currentLoginTime(UserMetricHelper.getCurrentLoginTime(map))
                .lastLoginTime(UserMetricHelper.getLastLoginTime(map))
                .yesterdayHasBet(UserMetricHelper.getYesterdayHasBet(map))
                .interventionRecord(UserMetricHelper.getInterventionRecord(map))
                .build();
    }
}
