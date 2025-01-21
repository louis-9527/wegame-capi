package com.wegame.capi.biz.module.mariamakiling.bet.strategy;

import cn.hutool.core.util.NumberUtil;
import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.MariaMakilingBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingBetDo;
import com.wegame.capi.biz.domain.biz.reward.RewardNotifyInfoDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.NotifyRewardTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;

@Slf4j
@Component("mariaMakilingNormalBetStrategy")
public class MariaMakilingNormalBetStrategy extends AbstractMariaMakilingBetStrategy {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @CacheEvict(value = CacheConstants.REWARD_INFO)
    @Override
    public void freeSpinsAfterHandle(BetDo<MariaMakilingBetDo> betDo, MariaMakilingBetRecordDetailDo recordDetail, Long totalRewardAmount) {
        if (recordDetail.isLastFreeSpins(betDo.getBetDetail().getFreeSpinsId())) {
            updateRewardNotifyInfo(betDo.getGameType(), recordDetail.freeSpinCount(), totalRewardAmount);
        }
    }

    private void updateRewardNotifyInfo(GameTypeEnum gameType, long freeSpinsCount, long totalRewardAmount) {
        try {
            // todo 测试日志,后面删除
            log.info("MariaMakilingNormalBetStrategy-updateRewardNotifyInfo触发:{},{}", freeSpinsCount, totalRewardAmount);
            UserInfo userInfo = UserInfoContext.getUserInfo();
            AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
            RewardNotifyInfoDo notifyInfo = RewardNotifyInfoDo.builder()
                    .gameId(GameTypeEnum.MARIA_MAKILING.getId())
                    .gameName(GameTypeEnum.MARIA_MAKILING.getName())
                    .userId(Long.valueOf(userInfo.getId()))
                    .userName(userInfo.getUserName())
                    .notifyRewardType(NotifyRewardTypeEnum.NORMAL_FREE_SPINS.name())
                    .rewardTime(DateUtil.getCurrentMilli())
                    .rewardContent(String.format(Constants.WEGAME_REWARD_NOTIFY_CONTENT_FORMAT,
                            DateUtil.format(new Date(), "yyyy/MM/dd HH:mm:ss"),
                            userInfo.getUserName(),
                            NumberUtil.div(totalRewardAmount, 100),
                            freeSpinsCount))
                    .build();
            String key = String.format(Constants.WEGAME_REWARD_NOTIFY_KEY_FORMAT, gameType.getId());
            DefaultRedisScript<Long> script = new DefaultRedisScript<>(Constants.WEGAME_REWARD_NOTIFY_LUA_SCRIPT, Long.class);
            redisTemplate.execute(script, Collections.singletonList(key), notifyInfo, Constants.WEGAME_REWARD_NOTIFY_SIZE);
        } catch (Exception e) {
            log.error("[MariaMakilingNonBuyFreeSpinsBetStrategy-notifyRewardHandle] error", e);
        }
    }
}
