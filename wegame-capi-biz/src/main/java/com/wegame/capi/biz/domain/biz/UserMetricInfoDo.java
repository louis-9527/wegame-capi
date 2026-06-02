package com.wegame.capi.biz.domain.biz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wegame.base.utils.DateUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class UserMetricInfoDo implements Serializable {

    private static final String common_spit = "_";
    // 注册时间
    public static final String user_metric_registerTime = "registerTime";
    // 上一次登陆时间
    public static final String user_metric_lastLoginTime = "lastLoginTime";
    // 进入游戏时余额
    public static final String user_metric_enterBalance = "enterBalance";
    // 退出游戏时余额
    public static final String user_metric_exitBalance = "exitBalance";
    // 当前余额
    public static final String user_metric_currentBalance = "currentBalance";
    // 总投注次数
    public static final String user_metric_totalBetTimes = "totalBetTimes";
    // 当日总注次数
    public static final String user_metric_todayBetTimes = "todayBetTimes";
    // 总投注金额
    public static final String user_metric_totalBetAmount = "totalBetAmount";
    // 总派彩金额
    public static final String user_metric_totalPayoutAmount = "totalPayoutAmount";
    // 空盘累计次数
    public static final String user_metric_continueEmptyTimes = "continueEmptyTimes";
    // 当日不中FG次数
    public static final String user_metric_todayNoFgTimes = "todayNoFgTimes";

    /**
     * 总投注次数
     */
    private Integer totalBetTimes;
    /**
     * 当日投注次数
     * <p>
     * 特殊说明：redis 存储格式：{YYYYMMDD}_{times}
     */
    private String todayBetTimesStr;
    /**
     * 总投注金额
     */
    private Long totalBetAmount;
    /**
     * 当日投注总金额
     */
    private Long totalPayoutAmount;
    /**
     * 连续不中奖次数
     */
    private Integer continueEmptyTimes;
    /**
     * 当日不中FG次数
     * <p>
     * 特殊说明：redis 存储格式：{YYYYMMDD}_{times} ***
     * <p>
     * times 为空：当日未累计
     * times 为0: 中fg后清0
     * times 大与0: 当日累计且大于0
     */
    private String todayContinueNoFgTimesStr;
    /**
     * 进入时余额
     */
    private BigDecimal enterBalance;
    /**
     * 退出时余额
     */
    private BigDecimal exitBalance;
    /**
     * 当前余额
     */
    private BigDecimal currentBalance;
    /**
     * 注册时间
     */
    private Date registerTime;
    /**
     * 上一次登陆时间
     */
    private Date lastLoginTime;

    public Integer getTotalBetTimes() {
        return Objects.isNull(totalBetTimes) ? 0 : totalBetTimes;
    }

    @JsonIgnore
    public int getTodayBetTimes() {
        if (StringUtils.isEmpty(todayBetTimesStr)) {
            return 0;
        }
        String[] arr = todayBetTimesStr.split(common_spit);
        String todayStr = DateUtil.getDateString(new Date());
        if (StringUtils.equals(arr[0], todayStr)) {
            return Integer.valueOf(arr[1]);
        } else {
            return 0;
        }
    }

    public Long getTotalBetAmount() {
        return Objects.isNull(totalBetAmount) ? 0 : totalBetAmount;
    }

    public Long getTotalPayoutAmount() {
        return Objects.isNull(totalPayoutAmount) ? 0L : totalPayoutAmount;
    }

    public Integer getContinueEmptyTimes() {
        return Objects.isNull(continueEmptyTimes) ? 0 : continueEmptyTimes;
    }

    ///** todo
    // * 当日不中FG次数
    // * <p>
    // * 特殊说明：redis 存储格式：{YYYYMMDD}_{times} *** todo
    // * <p>
    // * times 为空：当日未累计
    // * times 为0: 中fg后清0
    // * times 大与0: 当日累计且大于0
    // */
    @JsonIgnore
    public Integer getTodayContinueNoFgTimes() {
        if (StringUtils.isEmpty(todayContinueNoFgTimesStr)) {
            return 0;
        }
        String[] arr = todayContinueNoFgTimesStr.split(common_spit);
        String todayStr = DateUtil.getDateString(new Date());
        if (StringUtils.equals(arr[0], todayStr)) {
            return Integer.valueOf(arr[1]);
        } else {
            return 0;
        }
    }

    public BigDecimal getEnterBalance() {
        return enterBalance;
    }

    public BigDecimal getExitBalance() {
        return exitBalance;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    @JsonIgnore
    public boolean isYesterdayLogin() {
        return Objects.nonNull(lastLoginTime)
                && DateUtil.isBetween(lastLoginTime, DateUtil.getYesterdayBegin(), DateUtil.getYesterdayEnd());
    }

    @JsonIgnore
    public boolean isTodayRegister() {
        return Objects.nonNull(registerTime)
                && DateUtil.isBetween(registerTime, DateUtil.getTodayBegin(), DateUtil.getTodayEnd());
    }
}
