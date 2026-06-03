package com.wegame.capi.biz.domain.biz;

import com.wegame.base.utils.DateUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class UserMetricInfoDo implements Serializable {
    /**
     * 总投注次数
     */
    private int totalBetTimes;
    /**
     * 当日投注次数
     */
    private int todayBetTimes;
    /**
     * 总投注金额
     */
    private long totalBetAmount;
    /**
     * 当日投注总金额
     */
    private long totalPayoutAmount;
    /**
     * 连续不中奖次数
     */
    private int continueEmptyTimes;
    /**
     * 当日不中FG次数
     * 1、为空：当日未累计
     * 2、为0: 中fg后清0
     * 3、大与0: 当日累计且大于0
     */
    private Integer todayContinueNoFgTimes;
    /**
     * 进入时余额，可能为空
     */
    private Long enterBalance;
    /**
     * 退出时余额
     */
    private long exitBalance;
    /**
     * 当前余额，可能为空
     */
    private Long currentBalance;
    /**
     * 注册时间
     */
    private Date registerTime;
    /**
     * 上一次登陆时间
     */
    private Date lastLoginTime;
    /**
     * 干预记录 <ruleId, 时间戳>
     */
    private Map<String, Long> interventionRecord;

    public boolean isYesterdayLogin() {
        return Objects.nonNull(lastLoginTime)
                && DateUtil.isBetween(lastLoginTime, DateUtil.getYesterdayBegin(), DateUtil.getYesterdayEnd());
    }

    public boolean isTodayRegister() {
        return Objects.nonNull(registerTime)
                && DateUtil.isBetween(registerTime, DateUtil.getTodayBegin(), DateUtil.getTodayEnd());
    }

    public double getRtp() {
        if (totalBetAmount == 0) {
            return 0;
        }
        return totalPayoutAmount / (double) totalBetAmount;
    }
}
