package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.module.base.MetricHelper;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.dao.repository.domain.business.BetRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static com.wegame.capi.common.constants.MetricConstants.*;

/**
 * 用户埋点服务
 *
 * @author genereate
 */
@Slf4j
@Component
public class UserMetricService {
    private static final List<String> SUPPORT_GAME_ID_LIST = Arrays.asList(GameTypeEnum.LAM_ANG.getId());

    // user:metric:{gameId}:{pid}:{channelId}:{playerId}
    private static final String _KEY_FORMAT = "user:metric:%s:%s:%s:%s";

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    SingleWalletService singleWalletService;
    @Autowired
    BetRecordService betRecordService;

    /**
     * 登陆统计
     * 1、用户注册时间
     * 2、上一次登陆时间
     * 3、当前进入时余额
     * 4、上一次退出时余额
     * 5、当前余额
     */
    public void afterLoginMetric(UserInfo userInfo, Date registerTime, Date lastLoginTime) {
        if (!SUPPORT_GAME_ID_LIST.contains(userInfo.getGameId())) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put(user_metric_registerTime, registerTime);
        map.put(user_metric_lastLoginTime, lastLoginTime);
        BigDecimal credit = singleWalletService.getCredit(userInfo);
        map.put(user_metric_enterBalance, credit);
        map.put(user_metric_currentBalance, credit);
        try {
            BetRecordDo betRecordDo = betRecordService.get(BetRecordQuery.builder()
                            .playerId(userInfo.getPlayerId())
                            .status(BetStatusEnum.SUCCESS.getCode())
                            .orderByClause("createTime")
                            .limit(1)
                            .direction(Sort.Direction.DESC)
                            .build(),
                    GameTypeEnum.valueOfId(userInfo.getGameId()));
            map.put(user_metric_exitBalance, Objects.nonNull(betRecordDo) ? betRecordDo.getCredit() : null); // todo 余额单位
        } catch (Exception e) {
            log.error("afterLoginMetric error, userInfo: {}, map: {}", userInfo, e);
        }
        redisTemplate.opsForHash().putAll(key(userInfo), map);
    }

    /**
     * 投注统计
     * 1、总投注次数
     * 2、今日投注次数
     * 3、总投注金额
     * 4、总派彩金额
     * 5、空盘累计次数
     * 6、当日连续不中FG次数
     */
    //@Async(defaultThreadPoolExecutor)
    public void afterBetMetric(BetDo betDo, BetRecordDo betRecordDo, SingleWalletBetResultDo result, boolean hasRewarFreeWheel) {
        try {
            AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_INTER);
            AssertUtil.assertNotNull(result, ErrorCode.ERR_INTER);
            UserInfo userInfo = UserInfoContext.getUserInfo();
            AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
            if (!SUPPORT_GAME_ID_LIST.contains(userInfo.getGameId())) {
                return;
            }
            if (!Objects.equals(result.getBetStatus(), BetStatusEnum.SUCCESS)) {
                return;
            }
            if (!Objects.equals(betDo.getBetPromotionType(), BetPromotionTypeEnum.NONE)) {
                return;
            }
            // todo
            //UserMetricInfoDo userMetricInfoDo = get(userInfo);
            //UserMetricInfoDo newUserMetricInfoDo = UserMetricInfoBuilder.build(userMetricInfoDo, betRecordDo, result, hasRewarFreeWheel);
            //redisTemplate.opsForHash().putAll(key(userInfo), MapUtil.objectToMap(newUserMetricInfoDo));
        } catch (Exception e) {
            log.error("afterBetMetric error", e);
        }
    }

    //public static MetricHelper build(MetricHelper userMetricInfoDo, BetRecordDo betRecordDo, SingleWalletBetResultDo result, boolean hasRewarFreeWheel) {
    //    MetricHelper infoDo = new MetricHelper();
    //    infoDo.setTotalBetTimes(Objects.nonNull(userMetricInfoDo.getTotalBetTimes()) ? userMetricInfoDo.getTotalBetTimes() + 1 : 1);
    //    //infoDo.setTodayBetTimes(); todo
    //    infoDo.setTotalBetTimes(Objects.nonNull(userMetricInfoDo.getTodayBetTimes()) ? userMetricInfoDo.getTodayBetTimes() + 1 : 1);
    //    infoDo.setTotalBetAmount(Objects.nonNull(userMetricInfoDo.getTotalBetAmount()) ? userMetricInfoDo.getTotalBetAmount() + betRecordDo.getPayAmount() : betRecordDo.getPayAmount());
    //    infoDo.setTotalPayoutAmount(Objects.nonNull(userMetricInfoDo.getTotalPayoutAmount()) ? userMetricInfoDo.getTotalPayoutAmount() + betRecordDo.getPayOutAmount() : betRecordDo.getPayOutAmount());
    //    infoDo.setContinueEmptyTimes(betRecordDo.getPayOutAmount() > 0L ? 0 : infoDo.getContinueEmptyTimes() + 1);
    //    // todo
    //    infoDo.setTodayNoFgTimes(hasRewarFreeWheel ? 0 : (Objects.nonNull(userMetricInfoDo.getTodayContinueNoFgTimes()) ? userMetricInfoDo.getTodayContinueNoFgTimes() + 1 : 1));
    //    infoDo.setEnterBalance(userMetricInfoDo.getEnterBalance());
    //    infoDo.setExitBalance(userMetricInfoDo.getExitBalance());
    //    infoDo.setCurrentBalance(Objects.nonNull(result) ? AmountUtil.toBigDecimal(result.getCredit()) : null);
    //    infoDo.setRegisterTime(userMetricInfoDo.getRegisterTime());
    //    infoDo.setLastLoginTime(userMetricInfoDo.getLastLoginTime());
    //
    //    // todo
    //    return infoDo;
    //}

    public Map<String, Object> get(UserInfo userInfo) {
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return redisTemplate.opsForHash().entries(key(userInfo));
    }

    private String key(UserInfo userInfo) {
        return String.format(_KEY_FORMAT, userInfo.getGameId(), userInfo.getPId(), userInfo.getChannelId(), userInfo.getPlayerId());
    }
}