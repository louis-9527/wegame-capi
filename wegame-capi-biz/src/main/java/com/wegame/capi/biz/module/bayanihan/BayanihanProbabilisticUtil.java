package com.wegame.capi.biz.module.bayanihan;


import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.common.Constants;
import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.TreeMap;

public class BayanihanProbabilisticUtil {
    public static TreeMap<String, Integer> buildWildProbabilisticMap(TreeMap<String, BigDecimal> probabilisticConfigMap) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(probabilisticConfigMap), ErrorCode.ERR_INTER);
        TreeMap<String, Integer> map = new TreeMap<>();
        probabilisticConfigMap.forEach((key, value) -> {
            int val = (value.multiply(new BigDecimal(Constants.CONST_10000))).intValue();
            if (val > 0) {
                map.put(key, val);
            }
        });
        Integer sum = map.values()
                .stream()
                .reduce(0, Integer::sum);
        AssertUtil.assertTrue(sum == Constants.CONST_1000000, ErrorCode.ERR_INTER, "wild Symbol config error:");
        return map;
    }

}
