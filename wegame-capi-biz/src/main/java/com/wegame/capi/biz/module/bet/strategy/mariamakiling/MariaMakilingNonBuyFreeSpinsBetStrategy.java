package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import cn.hutool.core.util.NumberUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MariaMakilingGameRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingNonBuyFreeSpinsBetDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingNonBuyFreeSpinsBetResultDo;
import com.wegame.capi.biz.domain.biz.reward.MariaNotifyResultDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.PushGrandPrizeTypeEnum;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.dao.mapper.domain.user.BetRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component("mariaMakilingNonBuyFreeSpinsBetStrategy")
public class MariaMakilingNonBuyFreeSpinsBetStrategy
        extends AbstractMariaMakilingBetStrategy<MariaMakilingNonBuyFreeSpinsBetDo, MariaMakilingNonBuyFreeSpinsBetResultDo> {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public BetResultDo<MariaMakilingNonBuyFreeSpinsBetResultDo> bet(BetDo<MariaMakilingNonBuyFreeSpinsBetDo> betDo) {
        MariaMakilingNonBuyFreeSpinsBetDo betDetail = betDo.getBetDetail();
        if (betDetail.getBetRecordId() != null && betDetail.getFreeSpinsId() != null) {
            // 查询免费轮详情
            MariaMakilingGameRecordDetailDo gameBetResultDetailDo = betRecordService.getResultDetail(betDetail.getBetRecordId(), betDetail.getFreeSpinsId());
            if (Objects.nonNull(gameBetResultDetailDo) && gameBetResultDetailDo.isMaxFreeSpinsIdFlag()) {
                this.notifyPrizeHandle(betDo.getGameType(), betDetail.getBetRecordId());
            }
            return BetResultDo.<MariaMakilingNonBuyFreeSpinsBetResultDo>builder()
                    .betRecordId(betDetail.getBetRecordId())
                    .gameId(betDo.getGameId())
                    .betType(betDo.getBetType())
                    .betSubType(betDo.getBetSubType())
                    .resultDetail(MariaMakilingNonBuyFreeSpinsBetResultDo.builder()
                            .resultDetail(gameBetResultDetailDo)
                            .build())
                    .build();
        }

        Long recordId = Long.valueOf(betDo.getBetDetail().getBetRecordId());
        BetRecordDo betRecordDo = betRecordService.get(recordId);
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(BetSubTypeEnum.NON_BUY_FREE_SPINS_BET.getCode().equals(betRecordDo.getBetSubType()), ErrorCode.ERR_PARAM);

        // 必须是免费轮,并且免费轮未被核销
        long count = betRecordService.count(BetRecordQuery.builder().gameId(betDo.getGameId()).freeSpinsRefId(recordId).build());
        AssertUtil.assertTrue(count <= 0,
                ErrorCode.ERR_PARAM, "freeSpins status error,recordId:{}" + recordId);
        // 获取游戏结果
        List<MariaMakilingGameRecordDetailDo> resultDetails = super.getGameBetResult(betDo.getBetType(), betDo.getBetSubType(), 0L, Boolean.TRUE);
        long rewardAmount = 0L;
        List<Integer> freeSpinIds = new ArrayList<>(resultDetails.size());
        for (MariaMakilingGameRecordDetailDo resultDetail : resultDetails) {
            if (resultDetail.getRewardAmount() != null) {
                rewardAmount = rewardAmount + resultDetail.getRewardAmount();
            }
            freeSpinIds.add(resultDetail.getId());
        }
        // 获取投注结果

        long betRecordId = idGeneratorHelper.nextId();
        BetRecordDo betRecord = BetRecordBuilder.build(betDo, betRecordId, 0L, rewardAmount, resultDetails);
        betRecord.setFreeSpinsRefId(recordId);
        betRecord.setFreeSpinsTimes(resultDetails.size());
        // 保存投注记录并派奖
        super.saveBetRecordAndPayOut(betRecord);
        return BetResultDo.<MariaMakilingNonBuyFreeSpinsBetResultDo>builder()
                .betRecordId(betRecord.getId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .resultDetail(MariaMakilingNonBuyFreeSpinsBetResultDo.builder().freeSpinIds(freeSpinIds).build())
                .build();
    }

    void notifyPrizeHandle(GameTypeEnum gameType, Long freeSpinsBetRecordId) {
        try {
            UserInfo userInfo = UserInfoContext.getUserInfo();
            AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
            // 算出中奖金额返回。正常玩的记录派彩金额，免费轮的所有派彩金额
            BetRecordDo freeSpinsBetRecordDo = betRecordService.get(freeSpinsBetRecordId);
            AssertUtil.assertNotNull(freeSpinsBetRecordDo, ErrorCode.ERR_INTER, "freeSpinsBetRecord is not exist");
            BetRecordDo normalBetRecordDo = betRecordService.get(freeSpinsBetRecordDo.getFreeSpinsRefId());
            AssertUtil.assertNotNull(normalBetRecordDo, ErrorCode.ERR_INTER, "normalBetRecord is not exist");

            // 计算出所有金额
            Long normalPayOutAmount = Optional.ofNullable(normalBetRecordDo.getPayOutAmount()).orElse(0L);
            Long freeSpinsPayOutAmount = Optional.ofNullable(freeSpinsBetRecordDo.getPayOutAmount()).orElse(0L);
            MariaNotifyResultDo notifyResultDo = MariaNotifyResultDo.builder()
                    .gameTypeId(GameTypeEnum.MARIA_MAKILING.getId())
                    .userId(Long.valueOf(userInfo.getId()))
                    .userName(userInfo.getUserName())
                    .grandPrizeType(PushGrandPrizeTypeEnum.NORMAL_FREE_SPINS.name())
                    .grandTime(DateUtil.getCurrentMilli())
                    .tipsContent(String.format(Constants.PUSH_PRIZE_CONTENT_FORMAT, DateUtil.format(new Date(), "yyyy/MM/dd HH:mm:ss"),
                            userInfo.getUserName(), NumberUtil.div(normalPayOutAmount + freeSpinsPayOutAmount, 100), 10))
                    .build();
            String key = String.format(Constants.PUSH_PRIZE_REDIS_KEY, gameType.getId());
            DefaultRedisScript<Long> script = new DefaultRedisScript<>(Constants.PUSH_PRIZE_LUA_SCRIPT, Long.class);
            redisTemplate.execute(script, Collections.singletonList(key), notifyResultDo, Constants.PUSH_PRIZE_LIMIT);
        } catch (Exception e) {
            log.error("[MariaMakilingNonBuyFreeSpinsBetStrategy-notifyPrizeHandle] error,freeSpinsBetRecordId:{}", freeSpinsBetRecordId, e);
        }
    }
}
