package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户埋点服务
 *
 * @author genereate
 */
@Slf4j
@Component
public class UserMetricService {

    // user:metric:global:{gameId}:{pid}:{channelId}:{playerId}
    private static final String _GLOBAL_KEY_FORMAT = "user:metric:global:%s:%s:%s:%s";
    // user:metric:today:{gameId}:{pid}:{channelId}:{playerId}
    private static final String _TODAY_KEY_FORMAT = "user:metric:today:%s:%s:%s:%s";

    public static final String user_metric_registerTime = "registerTime";
    public static final String user_metric_lastLoginTime = "lastLoginTime";
    public static final String user_metric_enterBalance = "enterBalance";
    public static final String user_metric_currentBalance = "currentBalance";
    public static final String user_metric_exitBalance = "exitBalance";
    //public static final String user_metric_registerTime = "registerTime";
    //public static final String user_metric_registerTime = "registerTime";
    //public static final String user_metric_registerTime = "registerTime";
    ///**
    // * 总投注次数
    // */
    //private Integer totalBetTimes;
    ///**
    // * 当日投注次数
    // */
    //private Integer todayBetTimes;
    ///**
    // * 总投注金额
    // */
    //private Long totalBetAmount;
    ///**
    // * 当日投注总金额
    // */
    //private Long totalPayoutAmount;
    ///**
    // * 连续不中奖次数
    // */
    //private Integer continueEmptyTimes;
    ///**
    // * 当日不中FG次数
    // */
    //private Integer todayNoFgTimes;

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    SingleWalletService singleWalletService;

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

    /**
     * 登陆统计
     * 1、用户注册时间
     * 2、上一次登陆时间
     * 3、当前进入时余额
     * 4、上一次退出时余额
     * 5、当前余额
     */
    public void loginMetric(UserInfo userInfo, Date registerTime, Date lastLoginTime) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put(user_metric_registerTime, registerTime);
            map.put(user_metric_lastLoginTime, lastLoginTime);
            BigDecimal credit = singleWalletService.getCredit(userInfo);
            map.put(user_metric_enterBalance, credit);
            map.put(user_metric_currentBalance, credit);
            map.put(user_metric_exitBalance, credit); // todo
            redisTemplate.opsForHash().putAll(globalKey(userInfo), map);
        } catch (Exception e) {
            log.error("loginMetric error, userInfo: {}, map: {}", userInfo, e);
        }
    }

    public UserMetricInfoDo get() {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        Map<Object, Object> map = redisTemplate.opsForHash().entries(globalKey(userInfo));
        return FastJsonUtils.fromJSONString(FastJsonUtils.toJSONString(map), UserMetricInfoDo.class);
    }

    private String globalKey(UserInfo userInfo) {
        return String.format(_GLOBAL_KEY_FORMAT, userInfo.getGameId(), userInfo.getPId(), userInfo.getChannelId(), userInfo.getPlayerId());
    }

    private String todayKey(UserInfo userInfo) {
        return String.format(_TODAY_KEY_FORMAT, userInfo.getGameId(), userInfo.getPId(), userInfo.getChannelId(), userInfo.getPlayerId());
    }
}