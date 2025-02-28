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

    private void SecureRandomUtil(){
    }

    /**
     * 生成 [0, max] 区间的加密安全随机整数
     * @param max maxVal
     * @return randomVal
     */
    public static int randomInt(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max is less than 0");
        }
        return new SecureRandom().nextInt(max + 1);
    }

    /**
     * 批量生成 [0, max) 区间的加密安全随机整数
     */
    public static List<Integer> randomIntList(int max, int number) {
        if (max < 0) {
            throw new IllegalArgumentException("max is less than 0");
        }
        if (number < 0) {
            throw new IllegalArgumentException("number is less than 0");
        }
        List<Integer> randomList = new ArrayList<>();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < number; i++) {
            randomList.add(secureRandom.nextInt(max + 1));
        }
        return randomList;
    }

    /**
     * 生成介于 min 和 max 之间的加密安全随机整数 [min, max] 区间
     */
    public static Integer randomIntInRange(int min, int max) {
        if (min < 0) {
            throw new IllegalArgumentException("min is less than 0");
        }
        if (max < 0) {
            throw new IllegalArgumentException("max is less than 0");
        }
        if (min >= max) {
            throw new IllegalArgumentException("min must be less than max");
        }
        SecureRandom secureRandom = new SecureRandom();
        return min + secureRandom.nextInt(max - min + 1);
    }

    /**
     * 批量生成介于 min 和 max 之间的加密安全随机整数 [min, max] 区间
     * @param min minVal
     * @param max maxVal
     * @param number 数量
     * @return randomVal list
     */
    public static List<Integer> randomIntInRangeList(int min, int max, int number) {
        if (min < 0) {
            throw new IllegalArgumentException("min is less than 0");
        }
        if (max < 0) {
            throw new IllegalArgumentException("max is less than 0");
        }
        if (min >= max) {
            throw new IllegalArgumentException("min must be less than max");
        }
        List<Integer> randomList = new ArrayList<>();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < number; i++) {
            randomList.add(min + secureRandom.nextInt(max - min + 1));
        }
        return randomList;
    }

    /**
     * 生成 number 个不重复的随机整数 [min, max] 区间
     * @param min minVal
     * @param max maxVal
     * @param number 数量
     * @return randomVal set
     */
    public static Set<Integer> generateUniqueRandomNumbers(Integer min, Integer max, Integer number) {
        if (min < 0) {
            throw new IllegalArgumentException("min is less than 0");
        }
        if (max < 0) {
            throw new IllegalArgumentException("max is less than 0");
        }
        if (number > (max - min + 1)) {
            throw new IllegalArgumentException("range is too small to generate " + number + "unique numbers");
        }
        SecureRandom secureRandom = new SecureRandom();
        Set<Integer> uniqueRandomSet = new HashSet<>();
        while (uniqueRandomSet.size() < number) {
            int randomNumber = min + secureRandom.nextInt(max - min + 1);
            uniqueRandomSet.add(randomNumber);
        }
        return uniqueRandomSet;
    }

    /**
     * randomWeights
     * @param weights 权重
     * @return randomVal
     * @throws IllegalArgumentException
     */
    public static int randomWeights(int[] weights) throws IllegalArgumentException {
        if (weights.length < 1) {
            throw new IllegalArgumentException("weights length is < 1");
        } else if (weights.length == 1) {
            return 0;
        }
        // 计算所有权重之和
        int sumWeight = 0;
        for (int weight : weights) {
            sumWeight += weight;
        }
        if (sumWeight == 0) {
            return 0;
        }
        // 生成 [0, sum) 之间的随机数
        int r = new SecureRandom().nextInt(sumWeight);
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
