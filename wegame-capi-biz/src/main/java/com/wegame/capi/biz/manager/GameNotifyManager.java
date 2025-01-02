package com.wegame.capi.biz.manager;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.wegame.capi.client.domain.vo.GameNotifyVo;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.wegame.capi.common.Constants.CUSER_CREDIT_CHANGE;

@Service
public class GameNotifyManager {

    @Autowired
    RedisTemplate redisTemplate;

    public GameNotifyVo query() {
        GameNotifyVo vo = new GameNotifyVo();
        String messageStr = (String) redisTemplate.opsForValue().get(Constants.REWARD_MESSAGE);
        if (Objects.nonNull(messageStr) && JSONUtil.isTypeJSONObject(messageStr)) {
            vo.setMessages(JSON.parseArray(messageStr, String.class));
        }
        Object creditChange = redisTemplate.opsForValue().get(String.format(CUSER_CREDIT_CHANGE, UserInfoContext.getUserInfo().getPlayerId()));
        vo.setCreditChange(Objects.nonNull(creditChange) ? 1 : 0);
        return vo;
    }
}
