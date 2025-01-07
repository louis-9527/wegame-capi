package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import cn.hutool.core.util.NumberUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.IdGeneratorHelper;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.MariaMakilingBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingBetDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.MariaNotifyResultDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.PushGrandPrizeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("mariaMakilingNormalBetStrategy")
public class MariaMakilingNormalBetStrategy extends AbstractMariaMakilingBetStrategy {

    //    @Autowired
//    IdGeneratorHelper idGeneratorHelper;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
//
//    @Override
//    public BetResultDo<MariaMakilingGameRecordDo> bet(BetDo<MariaMakilingBetDo> betDo) {
//        MariaMakilingBetDo betDetail = betDo.getBetDetail();
//        if (Objects.nonNull(betDetail.getBetRecordId()) && Objects.nonNull(betDetail.getFreeSpinsId())) {
//
//            MariaMakilingBetRecordDetailDo betRecordDetailDo = super.queryGameRecord(betDetail.getBetRecordId(), betDo.getBetDetail().getFreeSpinsId());
//
//            if ( betRecordDetailDo.isLastFreeSpins(betDetail.getFreeSpinsId())) {
//                notifyPrizeHandle(betDo.getGameType(), betRecordDetailDo.freeSpinCount(), betRecordDetailDo.getPayOutTotalAmount());
//            }
//            return BetResultDo.<MariaMakilingGameRecordDo>builder()
//                    .betRecordId(betDetail.getBetRecordId())
//                    .gameId(betDo.getGameId())
//                    .betType(betDo.getBetType())
//                    .betSubType(betDo.getBetSubType())
//                    .resultDetail(betRecordDetailDo.getGameRecordList().get(0))
//                    .build();
//        }
//
//        List<MariaMakilingGameRecordDo> rets = new ArrayList<>();
//        List<MariaMakilingGameRecordDo> gameRecords = getGame().play(GamePlayDo.builder()
//                .gameType(betDo.getGameType())
//                .betType(betDo.getBetType())
//                .betSubType(betDo.getBetSubType())
//                .betAmount(betDo.getBetDetail().getBetAmount())
//                .freeSpinsFlag(false)
//                .build());
//        AssertUtil.assertNotNull(gameRecords, ErrorCode.ERR_INTER);
//        AssertUtil.assertNotEmpty(gameRecords, ErrorCode.ERR_INTER);
//        AssertUtil.assertTrue(gameRecords.size() == 1, ErrorCode.ERR_INTER);
//        MariaMakilingGameRecordDo gameRecord = gameRecords.get(0);
//        rets.addAll(gameRecords);
//        if (gameRecord.hasRewardFreeSpins()) {
//            List<MariaMakilingGameRecordDo> freeSpinsGameRecords = getGame().play(GamePlayDo.builder()
//                    .gameType(betDo.getGameType())
//                    .betType(betDo.getBetType())
//                    .betSubType(betDo.getBetSubType())
//                    .betAmount(betDo.getBetDetail().getBetAmount())
//                    .freeSpinsFlag(true)
//                    .build());
//
//            MariaMakilingGameRecordDo recordDo = rets.get(0);
//            recordDo.setFreeSpinIds(Optional.ofNullable(freeSpinsGameRecords)
//                    .orElse(Collections.emptyList())
//                    .stream()
//                    .map(item -> item.getIndex())
//                    .collect(Collectors.toList()));
//
//            rets.addAll(freeSpinsGameRecords);
//        }
//
//        // 构建投注记录
//        Long rewardAmount = rets.stream().filter(item -> item.getRewardResult() != null).mapToLong(item -> item.getRewardResult().getTotalRewardAmount()).sum();
//        long betRecordId = idGeneratorHelper.nextId();
//        BetRecordDo betRecord = BetRecordBuilder.build(betDo, betRecordId, betDo.getBetDetail().getBetAmount(), rewardAmount, MariaMakilingBetRecordDetailDo.builder()
//                .gameRecordList(rets)
//                .build());
//        Long id = betRecordService.add(betRecord);
//        AssertUtil.assertNotNull(id, ErrorCode.ERR_INTER);
//        // 单钱包投注并派彩 todo
//        // 修改投注状态 todo
//        // 组装返回结果
//        return BetResultDo.<MariaMakilingGameRecordDo>builder()
//                .betRecordId(betRecord.getId())
//                .gameId(betDo.getGameId())
//                .betType(betDo.getBetType())
//                .betSubType(betDo.getBetSubType())
//                .resultDetail(rets.get(0))
//                .build();
//    }


    @Override
    public void freeSpinsAfterHandle(BetDo<MariaMakilingBetDo> betDo, MariaMakilingBetRecordDetailDo recordDetail, Long totalRewardAmount) {
        if (recordDetail.isLastFreeSpins(betDo.getBetDetail().getFreeSpinsId())) {
            this.notifyPrizeHandle(betDo.getGameType(), recordDetail.freeSpinCount(), totalRewardAmount);
        }
    }

    // 正常玩 非免费轮
    void notifyPrizeHandle(GameTypeEnum gameType, long freeSpinsCount, long totalRewardAmount) {
        try {
            UserInfo userInfo = UserInfoContext.getUserInfo();
            AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
            MariaNotifyResultDo notifyResultDo = MariaNotifyResultDo.builder()
                    .gameTypeId(GameTypeEnum.MARIA_MAKILING.getId())
                    .userId(Long.valueOf(userInfo.getId()))
                    .userName(userInfo.getUserName())
                    .grandPrizeType(PushGrandPrizeTypeEnum.NORMAL_FREE_SPINS.name())
                    .grandTime(DateUtil.getCurrentMilli())
                    .tipsContent(String.format(Constants.PUSH_PRIZE_CONTENT_FORMAT, DateUtil.format(new Date(), "yyyy/MM/dd HH:mm:ss"),
                            userInfo.getUserName(), NumberUtil.div(totalRewardAmount, 100), freeSpinsCount))
                    .build();
            String key = String.format(Constants.PUSH_PRIZE_REDIS_KEY, gameType.getId());
            DefaultRedisScript<Long> script = new DefaultRedisScript<>(Constants.PUSH_PRIZE_LUA_SCRIPT, Long.class);
            redisTemplate.execute(script, Collections.singletonList(key), notifyResultDo, Constants.PUSH_PRIZE_LIMIT);
        } catch (Exception e) {
            log.error("[MariaMakilingNonBuyFreeSpinsBetStrategy-notifyPrizeHandle] error", e);
        }
    }
}
