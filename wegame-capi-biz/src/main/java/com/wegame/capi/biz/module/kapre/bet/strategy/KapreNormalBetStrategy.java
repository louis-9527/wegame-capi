package com.wegame.capi.biz.module.kapre.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.reward.RewardNotifyInfoDo;
import com.wegame.capi.biz.service.RewardService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.NotifyRewardTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component("kapreNormalBetStrategy")
public class KapreNormalBetStrategy extends AbstractKapreBetStrategy {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    RewardService rewardService;
    @Autowired
    ThreadPoolExecutor defaultThreadPoolExecutor;

    @Override
    public void freeWheelAfterHandle(Long betRecordId) {
       throw new RuntimeException("todo");
    }

    private void updateRewardNotifyInfo(long freeWheelCount, long totalRewardAmount, UserInfo userInfo) {
        try {
            log.info("freeWheelAfterHandle updateRewardNotifyInfo freeWheelCount: {}, totalRewardAmount: {}, userInfo: {}", freeWheelCount, totalRewardAmount, userInfo);
            AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
            RewardNotifyInfoDo notifyInfo = RewardNotifyInfoDo.builder()
                    .gameId(GameTypeEnum.KAPRE.getId())
                    .gameName(GameTypeEnum.KAPRE.getName())
                    .userId(Long.valueOf(userInfo.getId()))
                    .userName(userInfo.getUserName())
                    .notifyRewardType(NotifyRewardTypeEnum.NORMAL_FREE_WHEEL.name())
                    .rewardTime(DateUtil.getCurrentMilli())
                    .rewardContent(String.format(Constants.KAPRE_REWARD_NOTIFY_CONTENT_FORMAT,
                            DateUtil.format(new Date(), "yyyy/MM/dd HH:mm:ss"),
                            userInfo.getUserName(),
                            AmountUtil.decimalFormat("#,##0.###", AmountUtil.toBigDecimal(totalRewardAmount)),
                            freeWheelCount))
                    .build();
            String key = String.format(Constants.WEGAME_REWARD_NOTIFY_KEY_FORMAT, GameTypeEnum.KAPRE.getId());
            DefaultRedisScript<Long> script = new DefaultRedisScript<>(Constants.WEGAME_REWARD_NOTIFY_LUA_SCRIPT, Long.class);
            log.info("freeWheelAfterHandle updateRewardNotifyInfo add cache notifyInfo: {}, key: {}", notifyInfo, key);
            redisTemplate.execute(script, Collections.singletonList(key), notifyInfo, Constants.KAPRE_REWARD_NOTIFY_SIZE);
            rewardService.evict(GameTypeEnum.KAPRE);
            rewardService.getWithCache(GameTypeEnum.KAPRE);
        } catch (Exception e) {
            log.error("notifyRewardHandle error, errMsg: {}", e.getMessage(), e);
        }
    }
}
