//package com.wegame.capi.test.biz.module.mariamakiling.bet;
//
//import com.wegame.capi.biz.module.mariamakiling.bet.strategy.MariaMakilingNormalBetStrategy;
//import com.wegame.capi.test.BaseTest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//
//@Slf4j
//public class MariaMakilingNormalBetStrategyTest extends BaseTest {
//    @Autowired
//    MariaMakilingNormalBetStrategy mariaMakilingNormalBetStrategy;
//    @Autowired
//    RedisTemplate<String, Object> redisTemplate;
//
//
//    @Test
//    public void updateRewardNotifyInfo_test() {
//        UserInfoContext.setUserInfo(UserInfo.builder().id("1230000").userName("jayni大大").build());
//        for (int i = 0; i < 11; i++) {
//            mariaMakilingNormalBetStrategy.updateRewardNotifyInfo(GameTypeEnum.MARIA_MAKILING, 10L, 10000000L);
//        }
//        String key = String.format(Constants.WEGAME_REWARD_NOTIFY_KEY_FORMAT, GameTypeEnum.MARIA_MAKILING.getId());
//        List<Object> range = redisTemplate.opsForList().range(key, 0, -1);
//        Assert.assertNotNull(range);
//    }
//}
