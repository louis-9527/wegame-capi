package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.user.UserLoginInfoDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.dao.mapper.domain.user.UserLoginInfoQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户统计服务
 *
 * @author genereate
 */
@Slf4j
@Component
public class UserMetricService {

    // user:metric:{gameId}:{pid}:{channelId}:{playerId}
    private static final String format = "user:metric:%s:%s:%s:%s";
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserLoginInfoService userLoginInfoService;

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

    //public void beforeLoginMetric(UserInfo userInfo) {
    //    UserLoginInfoDo userLoginInfo = userLoginInfoService.selectOne(UserLoginInfoQuery.builder()
    //            .pId(userInfo.getPId())
    //            .channelId(Long.valueOf(userInfo.getChannelId()))
    //            .gameId(userInfo.getGameId())
    //            .userId(Long.valueOf(userInfo.getId()))
    //            .playerId(userInfo.getPlayerId())
    //            .build());
    //    Date loginTime = userLoginInfo.getLoginTime();
    //    // 统计昨日是否登陆。todo
    //}

    /**
     * 登陆统计
     * 1、用户注册时间
     * 2、上一次登陆时间
     * 3、当前进入时余额
     * 4、上一次退出时余额
     * 5、当前余额
     */
    public void loginMetric(UserInfo userInfo, Date registerTime, Date lastLoginTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("registerTime", registerTime);
        map.put("lastLoginTime", lastLoginTime);

        redisTemplate.opsForHash().putAll(key(u));
    }

    public UserMetricInfoDo get() {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key(userInfo));
        return FastJsonUtils.fromJSONString(FastJsonUtils.toJSONString(map), UserMetricInfoDo.class);
    }

    private String key(UserInfo userInfo) {
        return String.format(format, userInfo.getGameId(), userInfo.getPId(), userInfo.getChannelId(), userInfo.getPlayerId());
    }
}