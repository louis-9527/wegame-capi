package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.WeGameGoApi;
import com.wegame.capi.api.domain.req.wegamego.MinMaxRandomReq;
import com.wegame.capi.api.domain.req.wegamego.MultiMinMaxRandomReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public int randomInt(int min, int max) {
        AssertUtil.assertTrue(min < max, ErrorCode.ERR_INTER);
        //        Random random = new Random();
        //        return random.nextInt(max - min + 1) + min;
        return weGameGoApi.minMaxRandom(MinMaxRandomReq.builder().min(min).max(max).build());
    }

    /**
     * 生成随机数据[min, max]
     *
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public List<Integer> randomIntList(int min, int max, int count) {
        //       return SecureRandomUtil.randomIntInRangeList(min,max,count);
        return weGameGoApi.multiMinMaxRandom(MultiMinMaxRandomReq.builder().count(count).min(min).max(max).build());
    }
}
