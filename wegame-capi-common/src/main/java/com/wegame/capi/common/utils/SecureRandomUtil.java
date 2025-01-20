package com.wegame.capi.common.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 安全随机数工具类
 */
public class SecureRandomUtil {
    /**
     * 生成 [0, max) 区间的加密安全随机整数
     *
     * @param max
     * @return
     */
    public static Integer randomInt(Integer max) {
        if (max <= 0) {
            throw new IllegalArgumentException("max is <= 0");
        }
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextInt(max + 1);
    }

    /**
     * 批量生成 [0, max) 区间的加密安全随机整数
     */
    public static List<Integer> randomIntList(Integer max, Integer number) {
        if (max <= 0) {
            throw new IllegalArgumentException("max is <= 0");
        }
        if (number <= 0) {
            throw new IllegalArgumentException("number is <= 0");
        }

        List<Integer> resultList = new ArrayList<>();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < number; i++) {
            resultList.add(secureRandom.nextInt(max + 1));
        }
        return resultList;
    }

    /**
     * 生成介于 min 和 max 之间的加密安全随机整数 [min, max] 区间
     */
    public static Integer randomIntInRange(Integer min, Integer max) {
        if (min <= 0) {
            throw new IllegalArgumentException("min is <= 0");
        }
        if (max <= 0) {
            throw new IllegalArgumentException("max is <= 0");
        }
        if (min >= max) {
            throw new IllegalArgumentException("min must be less than max");

        }
        SecureRandom secureRandom = new SecureRandom();
        return min + secureRandom.nextInt(max - min + 1);
    }

    /**
     * 批量生成介于 min 和 max 之间的加密安全随机整数 [min, max] 区间
     */
    public static List<Integer> randomIntInRangeList(Integer min, Integer max, Integer number) {
        if (min < 0) {
            throw new IllegalArgumentException("min is < 0");
        }
        if (max <= 0) {
            throw new IllegalArgumentException("max is <= 0");
        }
        if (min >= max) {
            throw new IllegalArgumentException("min must be less than max");

        }
        List<Integer> resultList = new ArrayList<>();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < number; i++) {
            resultList.add(min + secureRandom.nextInt(max - min + 1));
        }
        return resultList;
    }

    /**
     * 生成 n 个不重复的随机整数 [min, max] 区间
     */
    public static Set<Integer> generateUniqueRandomNumbers(Integer min, Integer max, Integer number) {
        if (min < 0) {
            throw new IllegalArgumentException("min is < 0");
        }
        if (max <= 0) {
            throw new IllegalArgumentException("max is <= 0");
        }
        if (number > (max - min + 1)) {
            throw new IllegalArgumentException("range is too small to generate " + number + "unique numbers");
        }
        SecureRandom secureRandom = new SecureRandom();
        Set<Integer> uniqueNumbers = new HashSet<>();

        while (uniqueNumbers.size() < number) {
            int randomNumber = min + secureRandom.nextInt(max - min + 1);
            uniqueNumbers.add(randomNumber);  // HashSet 自动去重
        }
        return uniqueNumbers;

    }

    public static int randomWeights(int[] weights) throws IllegalArgumentException {
        if (weights.length < 1) {
            throw new IllegalArgumentException("weights length is < 1");
        } else if (weights.length == 1) {
            return 0;
        }
        // 计算所有权重之和
        int sum = 0;
        for (int weight : weights) {
            sum += weight;
        }

        if (sum == 0) {
            return 0;
        }

        // 生成 [0, sum) 之间的随机数
        SecureRandom secureRandom = new SecureRandom();
        int r = secureRandom.nextInt(sum);

        // 查找权重对应的下标
        int t = 0;
        for (int i = 0; i < weights.length; i++) {
            t += weights[i];
            if (t > r) {
                return i;
            }
        }
        return 0;
    }


}
