package com.wegame.capi.test.biz.module.bakunawa.multiple;

import com.wegame.capi.biz.module.bakunawa.multiple.BakunawaMultipleCalculateStrategy;
import com.wegame.capi.biz.module.mariamakiling.bet.strategy.MariaMakilingNormalBetStrategy;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.game.BakunawaGameRecordDo;
import com.wegame.capi.biz.module.bakunawa.bet.strategy.BakunawaNormalBetStrategy;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class MariaMakilingMultipleTest extends BaseTest {
    @Autowired
    BakunawaMultipleCalculateStrategy bakunawaMultipleCalculateStrategy;
    @Test
    public void updateRewardNotifyInfo_test() {
        //        UserInfoContext.setUserInfo(UserInfo.builder().id("1230000").userName("jayni大大").build());
        //        for (int i = 0; i < 11; i++) {
        //            mariaMakilingNormalBetStrategy.updateRewardNotifyInfo(GameTypeEnum.MARIA_MAKILING, 10L, 10000000L);
        //        }
        //        String key = String.format(Constants.WEGAME_REWARD_NOTIFY_KEY_FORMAT, GameTypeEnum.MARIA_MAKILING.getId());
        //        List<Object> range = redisTemplate.opsForList().range(key, 0, -1);
        //        Assert.assertNotNull(range);
    }
}
