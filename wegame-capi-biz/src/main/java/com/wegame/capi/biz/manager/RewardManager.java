package com.wegame.capi.biz.manager;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.wegame.capi.client.domain.vo.RewardInfoVo;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.wegame.capi.common.Constants.CUSER_CREDIT_CHANGE;

@Service
public class RewardManager {

    @Autowired
    RedisTemplate redisTemplate;

    public RewardInfoVo list() {
        RewardInfoVo vo = new RewardInfoVo();
        String messageStr = (String) redisTemplate.opsForValue().get(String.format(Constants.REWARD_MESSAGE, UserInfoContext.getUserInfo().getGameId()));
        if (Objects.nonNull(messageStr) && JSONUtil.isTypeJSONObject(messageStr)) {
            vo.setMessages(JSON.parseArray(messageStr, String.class));
        }
        Object creditChange = redisTemplate.opsForValue().get(String.format(CUSER_CREDIT_CHANGE, UserInfoContext.getUserInfo().getPlayerId()));
        vo.setCreditChange(Objects.nonNull(creditChange) ? 1 : 0);
        return vo;
    }
}
