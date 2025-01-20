package com.wegame.capi.biz.service;

import com.alibaba.fastjson.JSON;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.domain.biz.reward.RewardInfoDo;
import com.wegame.capi.biz.domain.biz.reward.RewardNotifyInfoDo;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfoContext;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RewardService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public RewardInfoDo get() {
        RewardInfoDo rewardInfoDo = new RewardInfoDo();
        String key = String.format(Constants.WEGAME_REWARD_NOTIFY_KEY_FORMAT, UserInfoContext.getUserInfo().getGameId());
        List<Object> messageList = redisTemplate.opsForList().range(key, 0, -1);
        if (CollectionUtils.isNotEmpty(messageList)) {
            List<RewardNotifyInfoDo> rewardNotifyInfoList = JSON.parseArray(FastJsonUtils.toJSONString(messageList), RewardNotifyInfoDo.class);
            rewardInfoDo.setRewardMessageList(CollectionUtils.isEmpty(rewardNotifyInfoList) ?
                    Collections.emptyList() : rewardNotifyInfoList.stream().map(RewardNotifyInfoDo::getRewardContent).collect(Collectors.toList()));
        }
        return rewardInfoDo;
    }
}
