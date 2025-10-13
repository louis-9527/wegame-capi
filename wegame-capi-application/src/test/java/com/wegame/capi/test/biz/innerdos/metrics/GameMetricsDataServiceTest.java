package com.wegame.capi.test.biz.innerdos.metrics;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Lists;
import com.wegame.capi.client.enums.innerdos.MetricsTypeEnum;
import com.wegame.capi.common.constants.MetricsConstants;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GameMetricsDataServiceTest extends BaseTest {
    private static final String LUA_INCREMENT_SCRIPT_STRING =
            "local startTime = redis.call('TIME')\n" +
                    "local hashKey = KEYS[1]\n" +
                    "local betAmountIncrement = tonumber(ARGV[1])\n" +
                    "local payoutAmountIncrement = tonumber(ARGV[2])\n" +
                    "local newBetAmount = redis.call('HINCRBY', hashKey, 'totalBetAmount', betAmountIncrement)\n" +
                    "local newPayoutAmount = redis.call('HINCRBY', hashKey, 'totalPayoutAmount', payoutAmountIncrement)\n" +
                    "local endTime = redis.call('TIME')\n" +
                    "local executionTime = (tonumber(endTime[1]) - tonumber(startTime[1])) * 1000000 + (tonumber(endTime[2]) - tonumber(startTime[2]))\n" +
                    "return {newBetAmount, newPayoutAmount, executionTime}";

    private static final String LUA_GET_SCRIPT_STRING =
            "local hashKey = KEYS[1]\n" +
                    "local values = redis.call('HMGET', hashKey, 'totalBetAmount', 'totalPayoutAmount')\n" +
                    "local betAmount = values[1] and tonumber(values[1]) or 0\n" +
                    "local payoutAmount = values[2] and tonumber(values[2]) or 0\n" +
                    "return {betAmount, payoutAmount}";

    private final DefaultRedisScript<List> LUA_INCREMENT_SCRIPT = new DefaultRedisScript<>();
    private final DefaultRedisScript<List> LUA_GET_SCRIPT = new DefaultRedisScript<>();

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        LUA_INCREMENT_SCRIPT.setScriptText(LUA_INCREMENT_SCRIPT_STRING);
        LUA_INCREMENT_SCRIPT.setResultType(List.class);

        LUA_GET_SCRIPT.setScriptText(LUA_GET_SCRIPT_STRING);
        LUA_GET_SCRIPT.setResultType(List.class);

    }

    @Test
    public void redisLuaTest() {

        String hashKey = MetricsConstants.buildGameMetricsCacheKey("50", "1060", MetricsTypeEnum.IBONG_ADARNA.getGameType().getId());


        Long totalBetAmountInc = 1000L;
        Long totalPayoutAmountInc = 1000L;

        int moreThanCostTimeCount_inc = 0;
        for (int i = 0; i < 1000000; i++) {
            long startTime = System.currentTimeMillis();
            // 构建lua脚本
            List<String> keys = Lists.newArrayList(hashKey);
            List<Long> args = Lists.newArrayList(totalBetAmountInc, totalPayoutAmountInc);
            List incResult = redisTemplate.execute(LUA_INCREMENT_SCRIPT, keys, args.toArray());
            long costTime = System.currentTimeMillis() - startTime;
            if (costTime > 5)
                log.info("redisLuaTest_lua_increment more than 5 cost time：{},count:{},innerCostTime:{}", costTime, ++moreThanCostTimeCount_inc, incResult.get(2));
        }

        int moreThanCostTimeCount_get = 0;
        for (int i = 0; i < 100; i++) {
            long startTime = System.currentTimeMillis();
            // 构建lua脚本
            List<String> keys = Lists.newArrayList(hashKey);
            redisTemplate.execute(LUA_GET_SCRIPT, keys);
            long costTime = System.currentTimeMillis() - startTime;
            if (costTime > 5)
                log.info("redisLuaTest_lua_get more than 5 cost time：{},count:{}", costTime, ++moreThanCostTimeCount_get);
        }
        redisTemplate.delete(hashKey);

    }

    @Test
    public void redisKeyTest() {
        int moreThanCostTimeCount_inc = 0;

        for (int i = 0; i < 1000000; i++) {
            long startTime = System.currentTimeMillis();
            String key = RandomUtil.randomString(10);
            String value = RandomUtil.randomString(10);
            redisTemplate.opsForValue().set(key, value, 5, TimeUnit.SECONDS);
            long costTime = System.currentTimeMillis() - startTime;
            if (costTime > 5)
                log.info("redisLuaTest_lua_increment more than 5 cost time：{},count:{}", costTime, ++moreThanCostTimeCount_inc);
        }
    }

}
