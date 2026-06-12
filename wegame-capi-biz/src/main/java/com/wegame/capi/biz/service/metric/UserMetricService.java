package com.wegame.capi.biz.service.metric;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.builder.UserMetricInfoBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.service.SingleWalletService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.UserMetricHelper;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static com.wegame.capi.common.constants.MetricConstants.*;
import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.metricThreadPoolExecutor;

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
    // {YYYYMMDD}:{times}
    private static final String _WITH_DATE_SPECIAL_VAL_FORMAT = "%s_%s";
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    SingleWalletService singleWalletService;
    @Autowired
    UserMetricHelperService userMetricHelperService;

    public UserMetricInfoDo getUserMetricInfo(UserInfo userInfo) {
        try {
            Map<String, Object> map = get(userInfo);
            log.info("user metric info from db, userInfo: {}, {}", userInfo, map);
            if (Objects.isNull(map)) {
                return null;
            }
            UserMetricInfoDo infoDo = UserMetricInfoBuilder.build(map);
            log.info("user metric info after convert, userInfo: {}, {}", userInfo, infoDo);
            return infoDo;
        } catch (Exception e) {
            log.error("getUserMetricInfo error, userInfo: {}", userInfo, e);
            return null;
        }
    }

    /**
     * 登陆统计
     * 1、用户注册时间
     * 2、上一次登陆时间
     * 3、当前进入时余额
     * 4、上一次退出时余额
     * 5、当前余额
     */
    @Async(metricThreadPoolExecutor)
    public void afterLoginMetric(UserInfo userInfo, Date registerTime, Date lastLoginTime, Date currentLoginTime) {
        if (!SUPPORT_GAME_ID_LIST.contains(userInfo.getGameId())) {
            return;
        }
        try {
            Map<String, Object> map = new HashMap<>();
            map.put(user_metric_registerTime, registerTime);
            map.put(user_metric_lastLoginTime, lastLoginTime);
            map.put(user_metric_currentLoginTime, currentLoginTime);
            BigDecimal credit = singleWalletService.getCredit(userInfo);
            map.put(user_metric_enterBalance, Objects.nonNull(credit) ? AmountUtil.toLong(credit) : null);
            map.put(user_metric_currentBalance, Objects.nonNull(credit) ? AmountUtil.toLong(credit) : null);
            BetRecordDo lastBetRecordDo = userMetricHelperService.getLastBetRecord(userInfo);
            map.put(user_metric_exitBalance, Objects.nonNull(lastBetRecordDo) ? lastBetRecordDo.getCredit() : null);
            Boolean yesterdayHasBet = userMetricHelperService.yesterdayHasBet(userInfo);
            map.put(user_metric_yesterdayHasBet, yesterdayHasBet);
            redisTemplate.opsForHash().putAll(key(userInfo), map);
        } catch (Exception e) {
            log.error("afterLoginMetric error, playerId: {}", userInfo.getPlayerId(), e);
        }
    }

    /**
     * 投注成功后统计(dos采集，freeSpin & 星卡不采集)
     * 1、总投注次数
     * 2、今日投注次数
     * 3、总投注金额(支付金额)
     * 4、总派彩金额
     * 5、空盘累计次数
     * 6、连续不中FG次数
     */
    @Async(metricThreadPoolExecutor)
    public void afterBetMetric(UserInfo userInfo, BetRecordDo betRecordDo, SingleWalletBetResultDo result, boolean hasRewarFreeWheel) {
        try {
            AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
            AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_INTER);
            AssertUtil.assertNotNull(result, ErrorCode.ERR_INTER);
            BetPromotionTypeEnum betPromotionType = BetPromotionTypeEnum.valueOfCode(betRecordDo.getBetPromotionType());
            AssertUtil.assertNotNull(betPromotionType, ErrorCode.ERR_INTER);
            if (!SUPPORT_GAME_ID_LIST.contains(userInfo.getGameId())
                    || !Objects.equals(result.getBetStatus(), BetStatusEnum.SUCCESS)
                    || !betPromotionType.supportUserMetric()) {
                return;
            }
            Map<String, Object> mapFromDb = get(userInfo);
            Map<String, Object> updateMap = buildUpdateMap(mapFromDb, betRecordDo, result, hasRewarFreeWheel);
            redisTemplate.opsForHash().putAll(key(userInfo), updateMap);
        } catch (Exception e) {
            log.error("afterBetMetric error, userInfo: {}", userInfo, e);
        }
    }

    private Map<String, Object> buildUpdateMap(Map<String, Object> mapFromDb, BetRecordDo betRecordDo, SingleWalletBetResultDo result, boolean hasRewarFreeWheel) {
        Map<String, Object> updateMap = new HashMap<>();
        long totalBetTimes = UserMetricHelper.getTotalBetTimes(mapFromDb) + 1;
        updateMap.put(user_metric_totalBetTimes, totalBetTimes);
        Date betTime = new Date(betRecordDo.getCreateTime());
        if (DateUtil.isBetween(betTime, DateUtil.getBeforeDate(), DateUtil.getTodayEnd())) {
            int todayBetTimes = UserMetricHelper.getTodayBetTimes(mapFromDb);
            updateMap.put(user_metric_todayBetTimes, String.format(_WITH_DATE_SPECIAL_VAL_FORMAT, DateUtil.getDateString(new Date()), todayBetTimes + 1));
        }
        long totalBetAmount = UserMetricHelper.getTotalBetAmount(mapFromDb) + betRecordDo.getPayAmount();
        updateMap.put(user_metric_totalBetAmount, totalBetAmount);
        long totalPayOutAmount = UserMetricHelper.getTotalPayoutAmount(mapFromDb) + betRecordDo.getPayOutAmount();
        updateMap.put(user_metric_totalPayoutAmount, totalPayOutAmount);
        int continueEmptyTimes = UserMetricHelper.getContinueEmptyTimes(mapFromDb);
        continueEmptyTimes = betRecordDo.getPayOutAmount() > 0 ? 0 : continueEmptyTimes + 1;
        updateMap.put(user_metric_continueEmptyTimes, continueEmptyTimes);
        if (StringUtils.equals(betRecordDo.getBetType(), BetTypeEnum.NORMAL_BET.getCode())) {
            // 仅正常投注，才统计fg未中次数
            int continueNoFgTimes = UserMetricHelper.getContinueNoFgTimes(mapFromDb);
            // 击中fg后次数清0
            continueNoFgTimes = hasRewarFreeWheel ? 0 : continueNoFgTimes + 1;
            updateMap.put(user_metric_continueNoFgTimes, continueNoFgTimes);
        }
        updateMap.put(user_metric_currentBalance, result.getCredit());
        log.info("extendInfo: {}", betRecordDo.getExtendInfo());
        if (Objects.nonNull(betRecordDo.getExtendInfo()) && StringUtils.isNotEmpty(betRecordDo.getExtendInfo().getInterventionRuleId())) {
            Map<String, Long> interventionRecord = UserMetricHelper.getInterventionRecord(mapFromDb);
            interventionRecord.put(betRecordDo.getExtendInfo().getInterventionRuleId(), betRecordDo.getCreateTime());
            updateMap.put(user_metric_interventionRecord, interventionRecord);
        }
        return updateMap;
    }

    private Map<String, Object> get(UserInfo userInfo) {
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        Map<String, Object> map = redisTemplate.opsForHash().entries(key(userInfo));
        return map;
    }

    private String key(UserInfo userInfo) {
        return String.format(_KEY_FORMAT, userInfo.getGameId(), userInfo.getPId(), userInfo.getChannelId(), userInfo.getPlayerId());
    }
}