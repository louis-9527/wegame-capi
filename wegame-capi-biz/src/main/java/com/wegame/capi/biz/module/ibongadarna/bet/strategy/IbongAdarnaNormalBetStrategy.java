package com.wegame.capi.biz.module.ibongadarna.bet.strategy;

import cn.hutool.core.util.NumberUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.IbongAdarnaBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.IbongAdarnaBetDo;
import com.wegame.capi.biz.domain.biz.reward.RewardNotifyInfoDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.NotifyRewardTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;

@Slf4j
@Component("ibongAdarnaNormalBetStrategy")
public class IbongAdarnaNormalBetStrategy extends AbstractIbongAdarnaBetStrategy {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void freeWheelAfterHandle(BetDo<IbongAdarnaBetDo> betDo, IbongAdarnaBetRecordDetailDo detailDo, Long totalRewardAmount) {
        if (detailDo.isLastFreeSpins(betDo.getBetDetail().getGameRecordId())) {
            updateRewardNotifyInfo(betDo.getGameType(), detailDo.freeWheelCount(), totalRewardAmount);
        }
    }

    private void updateRewardNotifyInfo(GameTypeEnum gameType, long freeSpinsCount, long totalRewardAmount) {
        try {
            UserInfo userInfo = UserInfoContext.getUserInfo();
            AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
            RewardNotifyInfoDo notifyInfo = RewardNotifyInfoDo.builder()
                    .gameId(GameTypeEnum.IBONG_ADARNA.getId())
                    .gameName(GameTypeEnum.IBONG_ADARNA.getName())
                    .userId(Long.valueOf(userInfo.getId()))
                    .userName(userInfo.getUserName())
                    .notifyRewardType(NotifyRewardTypeEnum.NORMAL_FREE_SPINS.name())
                    .rewardTime(DateUtil.getCurrentMilli())
                    .rewardContent(String.format(Constants.WEGAME_REWARD_NOTIFY_CONTENT_FORMAT,
                            DateUtil.format(new Date(), "yyyy/MM/dd HH:mm:ss"),
                            userInfo.getUserName(),
                            NumberUtil.decimalFormat("#,##0.00", NumberUtil.div(totalRewardAmount, 100)),
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
