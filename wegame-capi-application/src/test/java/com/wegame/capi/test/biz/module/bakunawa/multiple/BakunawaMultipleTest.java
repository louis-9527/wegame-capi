package com.wegame.capi.test.biz.module.bakunawa.multiple;

import com.wegame.capi.biz.module.bakunawa.multiple.BakunawaMultipleCalculateStrategy;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.junit.Test;

@Slf4j
public class BakunawaMultipleTest extends BaseTest {
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
