package com.wegame.capi.test.common;

import com.wegame.capi.common.utils.SecureRandomUtil;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class SecureRandomUtilTest {

    @Test
    public void test_randomInt() {
        // suc
        int max = 10;
        for (int i = 0; i < 100; i++) {
            int randomInt = SecureRandomUtil.randomInt(max);
            // System.out.println(randomInt);
            assertTrue(randomInt >= 0 && randomInt <= max);
        }
        // fail
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.randomInt(-1);
        });
        assertEquals("max is less than 0", thrown.getMessage());
    }

    @Test
    public void test_randomIntList() {
        // suc
        int max = 10;
        int number = 10;
        List<Integer> randomIntList = SecureRandomUtil.randomIntList(max, number);
        assertNotNull(randomIntList);
        assertEquals(number, randomIntList.size());
        // System.out.println("test_randomIntList: " + randomIntList);
        randomIntList.forEach(num -> assertTrue(num >= 0 && num <= max));
        // fail
        IllegalArgumentException thrown1 = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.randomIntList(-1, number);
        });
        assertEquals("max is less than 0", thrown1.getMessage());
        IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.randomIntList(max, 0);
        });
        assertEquals("number is less than 1", thrown2.getMessage());
    }

    @Test
    public void test_randomIntInRange() {
        // suc
        int min = 5;
        int max = 15;
        for (int i = 0; i < 10; i++) {
            int randomInt = SecureRandomUtil.randomIntInRange(min, max);
            // System.out.println("test_randomIntInRange: " + randomInt);
            assertNotNull(randomInt);
            assertTrue(randomInt >= min && randomInt <= max);
        }
        // fail
        IllegalArgumentException thrown1 = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.randomIntInRange(-1, max);
        });
        assertEquals("min is less than 0", thrown1.getMessage());
        IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.randomIntInRange(min, -1);
        });
        assertEquals("max is less than 0", thrown2.getMessage());
        IllegalArgumentException thrown3 = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.randomIntInRange(max, min);
        });
        assertEquals("min must be less than max", thrown3.getMessage());
    }

    @Test
    public void test_randomIntInRangeList() {
        // suc
        int min = 5;
        int max = 15;
        int number = 20;
        List<Integer> randomIntList = SecureRandomUtil.randomIntInRangeList(min, max, number);
        assertNotNull(randomIntList);
        assertEquals(number, randomIntList.size());
        // System.out.println("test_randomIntInRangeList: " + randomIntList);
        randomIntList.forEach(num -> assertTrue(num >= min && num <= max));
        // fail
        IllegalArgumentException thrown1 = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.randomIntInRangeList(-1, max, number);
        });
        assertEquals("min is less than 0", thrown1.getMessage());
        IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.randomIntInRangeList(min, -1, number);
        });
        assertEquals("max is less than 0", thrown2.getMessage());
        IllegalArgumentException thrown3 = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.randomIntInRangeList(min, max, 0);
        });
        assertEquals("number is less than 1", thrown3.getMessage());
        IllegalArgumentException thrown4 = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.randomIntInRangeList(max, min, number);
        });
        assertEquals("min must be less than max", thrown4.getMessage());
    }

    @Test
    public void test_generateUniqueRandomNumbers() {
        // suc
        int min = 1;
        int max = 20;
        int number = 5;
        Set<Integer> randomIntSet = SecureRandomUtil.generateUniqueRandomNumbers(min, max, number);
        assertEquals(number, randomIntSet.size());
        // System.out.println("test_generateUniqueRandomNumbers: " + randomIntSet);
        randomIntSet.forEach(num -> assertTrue(num >= min && num <= max));
        // fail
        IllegalArgumentException thrown1 = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.generateUniqueRandomNumbers(-1, max, number);
        });
        assertEquals("min is less than 0", thrown1.getMessage());
        IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.generateUniqueRandomNumbers(min, -1, number);
        });
        assertEquals("max is less than 0", thrown2.getMessage());
        IllegalArgumentException thrown3 = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.randomIntInRangeList(min, max, 0);
        });
        assertEquals("number is less than 1", thrown3.getMessage());
        IllegalArgumentException thrown4 = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.generateUniqueRandomNumbers(max, min, number);
        });
        assertEquals("min must be less than max", thrown4.getMessage());
        IllegalArgumentException thrown5 = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.generateUniqueRandomNumbers(min, max, max - min + 2);
        });
        assertEquals("min to max range is less than " + (max - min + 2), thrown5.getMessage());
    }

    @Test
    public void test_randomWeights() {
        // suc
        int[] weights = {1, 3, 5, 1};
        for (int i = 0; i <= 10; i++) {
            int index = SecureRandomUtil.randomWeights(weights);
            // System.out.println("test_randomWeights: " + index);
            assertTrue(index >= 0 && index < weights.length);
        }
        int zeroIndex = SecureRandomUtil.randomWeights(new int[]{1});
        assertTrue(zeroIndex == 0);
        // fail
        IllegalArgumentException thrown1 = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.randomWeights(null);
        });
        assertEquals("weights is null or empty", thrown1.getMessage());
        IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class, () -> {
            SecureRandomUtil.randomWeights(new int[]{});
        });
        assertEquals("weights is null or empty", thrown2.getMessage());
    }
}
