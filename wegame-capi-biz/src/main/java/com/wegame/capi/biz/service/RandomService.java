package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.WeGameGoApi;
import com.wegame.capi.api.domain.req.wegamego.MinMaxRandomReq;
import com.wegame.capi.api.domain.req.wegamego.MultiMinMaxRandomReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class RandomService {

    @Autowired
    WeGameGoApi weGameGoApi;

    /**
     * 生成随机数据[min, max]
     *
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public int randomIntOld(int min, int max) {
        Random random = new Random();
        AssertUtil.assertTrue(min < max, ErrorCode.ERR_INTER);
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * 生成随机数据[min, max]
     *
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public int randomInt(int min, int max) {
        Integer random = weGameGoApi.minMaxRandom(MinMaxRandomReq.builder().min(1).max(max - min + 1).build());
        return random + min;
    }

    /**
     * 生成多个随机数据[min, max]
     *
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public List<Integer> randomMultiInt(int count, int min, int max) {
        return weGameGoApi.multiMinMaxRandom(MultiMinMaxRandomReq.builder().count(count).min(1).max(max - min + 1).build());
    }
}
