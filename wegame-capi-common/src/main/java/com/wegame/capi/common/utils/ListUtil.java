package com.wegame.capi.common.utils;

import java.util.List;
import java.util.Random;

/**
 * 列表工具
 */
public class ListUtil {

    public static Long getRandomElement(List<Long> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(new Random().nextInt(list.size()));
    }
}
