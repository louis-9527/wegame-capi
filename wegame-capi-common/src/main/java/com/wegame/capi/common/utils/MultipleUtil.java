package com.wegame.capi.common.utils;

import java.util.List;

public class MultipleUtil {

    public static Double findClosestMultiple(double input, List<Double> multipleList) {
        if (input < 1.1) {
            return null;
        }
        int left = 0;
        int right = multipleList.size() - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (multipleList.get(mid) <= input) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result == -1 ? null : multipleList.get(result);
    }
}
