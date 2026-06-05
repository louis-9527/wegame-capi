package com.wegame.capi.common;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.FastJsonUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
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
    public static Long getEnterBalance(Map<String, Object> map) {
        AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_enterBalance);
        return Objects.isNull(obj) ? null : Long.valueOf(obj.toString());
    }

    /**
     * 退出时余额
     */
    public static long getExitBalance(Map<String, Object> map) {
        AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_exitBalance);
        return Objects.isNull(obj) ? null : Long.valueOf(obj.toString());
    }

    /**
     * 当前余额, 可能为空
     */
    public static Long getCurrentBalance(Map<String, Object> map) {
        AssertUtil.assertNotNull(map, ErrorCode.ERR_INTER);
        Object obj = map.get(user_metric_currentBalance);
        return Objects.isNull(obj) ? null : Long.valueOf(obj.toString());
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
        Object obj = map.get(user_metric_yesterdayLoginTime);
        return Objects.isNull(obj) ? null : (Date) obj;
    }

    /**
     * 干预记录
     */
    public static Map<String, Long> getInterventionRecord(Map<String, Object> map) {
        Object obj = map.get(user_metric_interventionRecord);
        return Objects.nonNull(obj) ? FastJsonUtils.fromJSONString(FastJsonUtils.toJSONString(obj), new TypeReference<Map<String, Long>>() {
        }) : new HashMap<>();
    }
}
