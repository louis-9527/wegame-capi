package com.wegame.capi.test.common;

import com.wegame.capi.common.utils.SecureRandomUtil;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class SecureRandomUtilTest {

    @Test
    public void testRandomInt() {
        int max = 10;
        Integer result = SecureRandomUtil.randomInt(max);
        assertNotNull(result);
        assertTrue(result >= 0 && result <= max);
    }

    @Test
    public void testRandomIntWithInvalidMax() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.randomInt(0);
        });
        assertEquals("max is <= 0", thrown.getMessage());
    }

    @Test
    public void testRandomIntList() {
        int max = 10;
        int number = 5;
        List<Integer> resultList = SecureRandomUtil.randomIntList(max, number);
        assertEquals(number, resultList.size());
        resultList.forEach(num -> assertTrue(num >= 0 && num <= max));
    }

    @Test
    public void testRandomIntInRange() {
        int min = 5;
        int max = 15;
        Integer result = SecureRandomUtil.randomIntInRange(min, max);
        assertNotNull(result);
        assertTrue(result >= min && result <= max);
    }

    @Test
    public void testRandomIntInRangeList() {
        int min = 5;
        int max = 15;
        int number = 5;
        List<Integer> resultList = SecureRandomUtil.randomIntInRangeList(min, max, number);
        assertEquals(number, resultList.size());
        resultList.forEach(num -> assertTrue(num >= min && num <= max));
    }

    @Test
    public void testGenerateUniqueRandomNumbers() {
        int min = 1;
        int max = 20;
        int number = 5;
        Set<Integer> resultSet = SecureRandomUtil.generateUniqueRandomNumbers(min, max, number);
        assertEquals(number, resultSet.size());
        resultSet.forEach(num -> assertTrue(num >= min && num <= max));
    }

    @Test
    public void testGenerateUniqueRandomNumbersWithInvalidRange() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.generateUniqueRandomNumbers(1, 5, 6);
        });
        assertEquals("range is too small to generate 6unique numbers", thrown.getMessage());
    }

    @Test
    public void testRandomWeights() {
        int[] weights = {1, 3, 5, 1};
        int index = SecureRandomUtil.randomWeights(weights);
        assertTrue(index >= 0 && index < weights.length);
    }

    @Test
    public void testRandomWeightsWithSingleWeight() {
        int[] weights = {5};
        int index = SecureRandomUtil.randomWeights(weights);
        assertEquals(0, index);
    }

    @Test
    public void testRandomWeightsWithInvalidWeights() {
        int[] weights = {};
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.randomWeights(weights);
        });
        assertEquals("weights length is < 1", thrown.getMessage());
    }

}
