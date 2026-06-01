package com.wegame.capi.biz.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 用户统计服务
 *
 * @author genereate
 */
@Slf4j
@Component
public class UserMetricService {
    @Autowired
    RedisTemplate redisTemplate;

    //    - 累计游戏局数，只计算在本游戏的投注次数。
    //            - 总投注，只计算在本游戏中的投注金额。
    //            - 总派彩，只计算在本游戏中的派彩金额。 不计算jackpot
    //- 连续空盘计数，每次空盘+1，中奖后清0。
    //
    //            - 当日不中FG次数，每次不中+1，击中后清0，当日不再累计。
    //            - 最后离开时余额：玩家登录后，保存最后投注时的余额。
    //            - 今日游戏局数
    //- 本次进入游戏余额：带了多少钱进入游戏
    //- 当前余额
    // 注册时间


}