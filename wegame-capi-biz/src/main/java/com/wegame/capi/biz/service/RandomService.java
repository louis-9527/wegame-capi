package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomService {

    private Random random = new Random();

    /**
     * 生成随机数据[min, max]
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public int randomInt(int min, int max) {
        AssertUtil.assertTrue(min < max, ErrorCode.ERR_INTER);
        return random.nextInt(max - min + 1) + min;
    }
}
