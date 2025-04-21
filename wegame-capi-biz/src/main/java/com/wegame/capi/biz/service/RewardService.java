package com.wegame.capi.biz.service;

import com.wegame.base.CacheConstants;
import com.wegame.capi.biz.domain.biz.reward.RewardInfoDo;
import com.wegame.capi.biz.domain.biz.reward.RewardNotifyInfoDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RewardService {

    @Autowired
    RedisTemplate redisTemplate;

    @Cacheable(value = CacheConstants.REWARD_INFO, key = "#gameType.name + '_'  + #gameType.id")
    public RewardInfoDo getWithCache(GameTypeEnum gameType) {
        RewardInfoDo rewardInfoDo = new RewardInfoDo();
        try {
            String key = String.format(Constants.WEGAME_REWARD_NOTIFY_KEY_FORMAT, gameType.getId());
            List<RewardNotifyInfoDo> messageList = (List<RewardNotifyInfoDo>) redisTemplate.opsForList().range(key, 0, -1);
            if (CollectionUtils.isNotEmpty(messageList)) {
                rewardInfoDo.setRewardMessageList(messageList.stream()
                        .map(RewardNotifyInfoDo::getRewardContent)
                        .collect(Collectors.toList()));
            }
        } catch (Exception e) {
            log.error("get rewardInfo error, gameType: {}", gameType, e);
        }
        return rewardInfoDo;
    }

    @CacheEvict(value = CacheConstants.REWARD_INFO, key = "#gameType.name + '_'  + #gameType.id")
    public void evict(GameTypeEnum gameType) {
        // do nothing
    }
}
