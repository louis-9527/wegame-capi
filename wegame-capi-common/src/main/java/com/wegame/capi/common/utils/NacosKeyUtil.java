package com.wegame.capi.common.utils;


import java.math.BigDecimal;

public class NacosKeyUtil {
    private static final String FROM_POINT_REPLACE_SPLIT="P";
    private static final String TARGET_POINT_DATA_FORMAT=".";

    public static BigDecimal transformToDecimal(String key) {
       return new BigDecimal(key.replace(FROM_POINT_REPLACE_SPLIT,TARGET_POINT_DATA_FORMAT));
    }
}
