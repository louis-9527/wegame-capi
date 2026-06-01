package com.wegame.capi.biz.domain.biz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
    private Integer totalBetTimes;
    /**
     * 当日投注次数
     */
    private Integer todayBetTimes;
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
     */
    private Integer todayNoFgTimes;
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


    @JsonIgnore
    public boolean isYesterdayLogin() {
        return true; // todo @bruce
    }

    @JsonIgnore
    public boolean isTodayRegister() {
        return true; // todo @bruce
    }

}
