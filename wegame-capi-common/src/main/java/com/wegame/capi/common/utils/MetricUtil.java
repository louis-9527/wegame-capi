package com.wegame.capi.common.utils;

import com.wegame.base.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class MetricUtil {

    public static int getTodayBetTimes(String todayBetTimesStr) {
        if (StringUtils.isEmpty(todayBetTimesStr)) {
            return 0;
        }
        String[] arr = todayBetTimesStr.split("_");
        String todayStr = DateUtil.getDateString(new Date());
        if (StringUtils.equals(arr[0], todayStr)) {
            return Integer.valueOf(arr[1]);
        } else {
            return 0;
        }
    }

}
