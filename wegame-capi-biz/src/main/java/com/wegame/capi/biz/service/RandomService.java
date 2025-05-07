package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.common.utils.SecureRandomUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RandomService {

    /**
     * 生成随机数据[min, max]
     *
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public int randomInt(int min, int max) {
        AssertUtil.assertTrue(min < max, ErrorCode.ERR_INTER);
        return SecureRandomUtil.randomIntInRange(min, max);
    }

    /**
     * 生成随机数据[min, max]
     *
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public List<Integer> randomIntList(int min, int max, int count) {
        return SecureRandomUtil.randomIntInRangeList(min, max, count);
    }
}
