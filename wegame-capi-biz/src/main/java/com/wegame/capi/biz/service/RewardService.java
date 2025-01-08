package com.wegame.capi.biz.service;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.wegame.capi.biz.domain.biz.reward.RewardInfoDo;
import com.wegame.capi.client.domain.vo.RewardInfoVo;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.wegame.capi.common.Constants.CUSER_CREDIT_CHANGE;

@Service
public class RewardService {

    @Autowired
    RedisTemplate redisTemplate;

    public RewardInfoDo get() {
        RewardInfoDo rewardInfoDo = new RewardInfoDo();
        String messageStr = (String) redisTemplate.opsForValue().get(String.format(Constants.REWARD_MESSAGE, UserInfoContext.getUserInfo().getGameId()));
        if (Objects.nonNull(messageStr) && JSONUtil.isTypeJSONObject(messageStr)) {
            rewardInfoDo.setRewardMessageList(JSON.parseArray(messageStr, String.class));
        }
        // todo fix set 数据结构
        Object creditChange = redisTemplate.opsForValue().get(String.format(CUSER_CREDIT_CHANGE, UserInfoContext.getUserInfo().getPlayerId()));
        rewardInfoDo.setUserCreditChange(Objects.nonNull(creditChange) ? 1 : 0);
        return rewardInfoDo;
    }
}
