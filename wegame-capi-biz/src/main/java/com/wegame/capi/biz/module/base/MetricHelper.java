package com.wegame.capi.biz.module.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static com.wegame.capi.common.constants.MetricConstants.*;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class MetricHelper implements Serializable {

    /**
     * 总投注次数
     */
    public int getTotalBetTimes(Map<String, Object> map) {
        Object obj = map.get(user_metric_totalBetTimes);
        return Objects.isNull(obj) ? 0 : (int) obj;
    }

    /**
     * 当日投注总金额
     */
    public int getTodayBetTimes(Map<String, Object> map) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_todayBetTimes);
        if (Objects.isNull(obj)) {
            return 0;
        }
        String[] arr = obj.toString().split(common_spit);
        String todayStr = DateUtil.getDateString(new Date());
        if (StringUtils.equals(arr[0], todayStr)) {
            return Integer.valueOf(arr[1]);
        } else {
            return 0;
        }
    }

    /**
     * 当日投注总金额
     */
    public long getTotalBetAmount(Map<String, Object> map) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_totalBetAmount);
        return Objects.isNull(obj) ? 0 : (long) obj;
    }

    /**
     * 总投派彩金额
     */
    public long getTotalPayoutAmount(Map<String, Object> map) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_totalPayoutAmount);
        return Objects.isNull(obj) ? 0 : (long) obj;
    }

    /**
     * 连续空盘次数
     */
    public int getContinueEmptyTimes(Map<String, Object> map) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_continueEmptyTimes);
        return Objects.isNull(obj) ? 0 : (int) obj;
    }

    /// ** todo
    // * 当日不中FG次数
    // * <p>
    // * 特殊说明：redis 存储格式：{YYYYMMDD}_{times} *** todo
    // * <p>
    // * times 为空：当日未累计
    // * times 为0: 中fg后清0
    // * times 大与0: 当日累计且大于0
    // */
    @JsonIgnore
    public Integer getTodayContinueNoFgTimes(Map<String, Object> map) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_todayContinueNoFgTimes);
        if (Objects.isNull(obj)) {
            return null;
        }
        String[] arr = obj.toString().split(common_spit);
        String todayStr = DateUtil.getDateString(new Date());
        if (StringUtils.equals(arr[0], todayStr)) {
            return Integer.valueOf(arr[1]);
        } else {
            return 0;
        }
    }

    /**
     * 进入时余额
     */
    public BigDecimal getEnterBalance(Map<String, Object> map) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_enterBalance);
        return Objects.isNull(obj) ? null : new BigDecimal(obj.toString());
    }

    /**
     * 退出时余额
     */
    public BigDecimal getExitBalance(Map<String, Object> map) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_exitBalance);
        return Objects.isNull(obj) ? null : new BigDecimal(obj.toString());
    }

    /**
     * 当前余额
     */
    public BigDecimal getCurrentBalance(Map<String, Object> map) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_currentBalance);
        return Objects.isNull(obj) ? null : new BigDecimal(obj.toString());
    }

    /**
     * 注册时间
     */
    public Date getRegisterTime(Map<String, Object> map) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_registerTime);
        return Objects.isNull(obj) ? null : (Date) obj;
    }

    /**
     * 上一次登陆时间
     */
    public Date getLastLoginTime(Map<String, Object> map) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_lastLoginTime);
        return Objects.isNull(obj) ? null : (Date) obj;
    }

    public boolean isYesterdayLogin(Map<String, Object> map) {
        Date yesterdayTime = getLastLoginTime(map);
        return Objects.nonNull(yesterdayTime)
                && DateUtil.isBetween(yesterdayTime, DateUtil.getYesterdayBegin(), DateUtil.getYesterdayEnd());
    }

    public boolean isTodayRegister(Map<String, Object> map) {
        Date registerTime = getRegisterTime(map);
        return Objects.nonNull(registerTime)
                && DateUtil.isBetween(registerTime, DateUtil.getTodayBegin(), DateUtil.getTodayEnd());
    }
}
