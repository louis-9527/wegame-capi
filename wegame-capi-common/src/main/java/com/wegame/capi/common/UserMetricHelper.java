package com.wegame.capi.common;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static com.wegame.capi.common.constants.MetricConstants.*;

@Slf4j
@Data
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class UserMetricHelper implements Serializable {

    /**
     * 总投注次数
     */
    public static int getTotalBetTimes(Map<String, Object> map) {
        Object obj = map.get(user_metric_totalBetTimes);
        return Objects.nonNull(obj) && NumberUtils.isDigits(obj.toString()) ? NumberUtils.toInt(obj.toString()) : 0;
    }

    /**
     * 当日投注总金额
     */
    public static int getTodayBetTimes(Map<String, Object> map) {
        AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_todayBetTimes);
        if (Objects.isNull(obj)) {
            return 0;
        }
        String[] arr = obj.toString().split(common_spit);
        if (arr.length != 2) {
            log.error("getTodayBetTimes format error, todayBetTimes: {}", obj);
            return 0;
        }
        String todayStr = DateUtil.getDateString(new Date());
        if (StringUtils.equals(arr[0], todayStr)) {
            return Integer.valueOf(arr[1]);
        } else {
            return 0;
        }
    }

    /**
     * 总投注总金额
     */
    public static long getTotalBetAmount(Map<String, Object> map) {
        AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_totalBetAmount);
        return Objects.nonNull(obj) && NumberUtils.isDigits(obj.toString()) ? NumberUtils.toLong(obj.toString()) : 0;
    }

    /**
     * 总派彩金额
     */
    public static long getTotalPayoutAmount(Map<String, Object> map) {
        AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_totalPayoutAmount);
        return Objects.nonNull(obj) && NumberUtils.isDigits(obj.toString()) ? NumberUtils.toLong(obj.toString()) : 0;
    }

    /**
     * 连续空盘次数
     */
    public static int getContinueEmptyTimes(Map<String, Object> map) {
        AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_continueEmptyTimes);
        return Objects.nonNull(obj) && NumberUtils.isDigits(obj.toString()) ? NumberUtils.toInt(obj.toString()) : 0;
    }

    /**
     * 当日不中FG次数
     * <p>
     * 特殊说明：redis 存储格式：{YYYYMMDD}_{times} ***
     * <p>
     *
     * @return 1、 为空：当日未累计
     * 2、 为0: 中fg后清0, 清0后当日不可再累计
     * 3、 大于0: 当日累计且大于0（未清0）
     */
    public static Integer getTodayContinueNoFgTimes(Map<String, Object> map) {
        AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_todayContinueNoFgTimes);
        if (Objects.isNull(obj)) {
            return null;
        }
        String[] arr = obj.toString().split(common_spit);
        if (arr.length != 2) {
            log.error("getTodayContinueNoFgTimes format error, todayContinueNoFgTimes: {}", obj);
            return null;
        }
        String todayStr = DateUtil.getDateString(new Date());
        if (StringUtils.equals(arr[0], todayStr)) {
            return Integer.valueOf(arr[1]);
        } else {
            return null;
        }
    }

    /**
     * 进入时余额, 可能为空
     */
    public static BigDecimal getEnterBalance(Map<String, Object> map) {
        AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_enterBalance);
        return Objects.isNull(obj) ? null : new BigDecimal(obj.toString());
    }

    /**
     * 退出时余额
     */
    public static BigDecimal getExitBalance(Map<String, Object> map) {
        AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_exitBalance);
        return Objects.isNull(obj) ? BigDecimal.ZERO : new BigDecimal(obj.toString());
    }

    /**
     * 当前余额, 可能为空
     */
    public static BigDecimal getCurrentBalance(Map<String, Object> map) {
        AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_currentBalance);
        return Objects.isNull(obj) ? null : new BigDecimal(obj.toString());
    }

    /**
     * 注册时间
     */
    public static Date getRegisterTime(Map<String, Object> map) {
        AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_registerTime);
        return Objects.isNull(obj) ? null : (Date) obj;
    }

    /**
     * 上一次登陆时间
     */
    public static Date getLastLoginTime(Map<String, Object> map) {
        AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_lastLoginTime);
        return Objects.isNull(obj) ? null : (Date) obj;
    }

    //public static boolean isYesterdayLogin(Map<String, Object> map) {
    //    AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
    //    Date yesterdayTime = getLastLoginTime(map);
    //    return Objects.nonNull(yesterdayTime)
    //            && DateUtil.isBetween(yesterdayTime, DateUtil.getYesterdayBegin(), DateUtil.getYesterdayEnd());
    //}
    //
    //public static boolean isTodayRegister(Map<String, Object> map) {
    //    AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
    //    Date registerTime = getRegisterTime(map);
    //    return Objects.nonNull(registerTime)
    //            && DateUtil.isBetween(registerTime, DateUtil.getTodayBegin(), DateUtil.getTodayEnd());
    //}
    //
    //public static double getRtp(Map<String, Object> map) {
    //    AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
    //    long payOutAmount = getTotalPayoutAmount(map);
    //    long totalBetAmount = getTotalBetAmount(map);
    //    if (totalBetAmount == 0) {
    //        return 0;
    //    }
    //    return payOutAmount / totalBetAmount;
    //}
}
