package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.common.utils.AmountUtil;

import java.util.Objects;

public class UserMetricInfoBuilder {

    public static UserMetricInfoDo build(UserMetricInfoDo userMetricInfoDo, BetRecordDo betRecordDo, SingleWalletBetResultDo result, boolean hasRewarFreeWheel) {
        UserMetricInfoDo infoDo = new UserMetricInfoDo();
        infoDo.setTotalBetTimes(Objects.nonNull(userMetricInfoDo.getTotalBetTimes()) ? userMetricInfoDo.getTotalBetTimes() + 1 : 1);
        //infoDo.setTodayBetTimes(); todo
        infoDo.setTotalBetTimes(Objects.nonNull(userMetricInfoDo.getTodayBetTimes()) ? userMetricInfoDo.getTodayBetTimes() + 1 : 1);
        infoDo.setTotalBetAmount(Objects.nonNull(userMetricInfoDo.getTotalBetAmount()) ? userMetricInfoDo.getTotalBetAmount() + betRecordDo.getPayAmount() : betRecordDo.getPayAmount());
        infoDo.setTotalPayoutAmount(Objects.nonNull(userMetricInfoDo.getTotalPayoutAmount()) ? userMetricInfoDo.getTotalPayoutAmount() + betRecordDo.getPayOutAmount() : betRecordDo.getPayOutAmount());
        infoDo.setContinueEmptyTimes(betRecordDo.getPayOutAmount() > 0L ? 0 : infoDo.getContinueEmptyTimes() + 1);
        // todo
        infoDo.setTodayNoFgTimes(hasRewarFreeWheel ? 0 : (Objects.nonNull(userMetricInfoDo.getTodayContinueNoFgTimes()) ? userMetricInfoDo.getTodayContinueNoFgTimes() + 1 : 1));
        infoDo.setEnterBalance(userMetricInfoDo.getEnterBalance());
        infoDo.setExitBalance(userMetricInfoDo.getExitBalance());
        infoDo.setCurrentBalance(Objects.nonNull(result) ? AmountUtil.toBigDecimal(result.getCredit()) : null);
        infoDo.setRegisterTime(userMetricInfoDo.getRegisterTime());
        infoDo.setLastLoginTime(userMetricInfoDo.getLastLoginTime());

        // todo
        return infoDo;
    }
}
