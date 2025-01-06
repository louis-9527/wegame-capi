package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.IdGeneratorHelper;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.GamePlayDo;
import com.wegame.capi.biz.domain.biz.MariaMakilingBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingBetDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component("mariaMakilingNormalBetStrategy")
public class MariaMakilingNormalBetStrategy
        extends AbstractMariaMakilingBetStrategy<MariaMakilingBetDo, MariaMakilingGameRecordDo> {

    @Autowired
    IdGeneratorHelper idGeneratorHelper;

    @Override
    public BetResultDo<MariaMakilingGameRecordDo> bet(BetDo<MariaMakilingBetDo> betDo) {
        MariaMakilingBetDo betDetail = betDo.getBetDetail();
        if (Objects.nonNull(betDetail.getBetRecordId()) && Objects.nonNull(betDetail.getFreeSpinsId())) {
            // 查询免费轮详情
            MariaMakilingBetRecordDetailDo betRecordDetailDo = betRecordService.getResultDetail(betDetail.getBetRecordId(), betDetail.getFreeSpinsId());
            // 如果最后一次查询则，返回总的中奖金额 todo
            return BetResultDo.<MariaMakilingGameRecordDo>builder()
                    .betRecordId(betDetail.getBetRecordId())
                    .gameId(betDo.getGameId())
                    .betType(betDo.getBetType())
                    .betSubType(betDo.getBetSubType())
                    .resultDetail(betRecordDetailDo.getGameRecordList()
                            .stream()
                            .filter(item -> item.getIndex() == betDetail.getFreeSpinsId())
                            .findFirst()
                            .orElse(null))
                    .build();
        }

        List<MariaMakilingGameRecordDo> rets = new ArrayList<>();
        List<MariaMakilingGameRecordDo> gameRecords = getGame().play(GamePlayDo.builder()
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(betDo.getBetDetail().getBetAmount())
                .freeSpinsFlag(false)
                .build());
        AssertUtil.assertNotNull(gameRecords, ErrorCode.ERR_INTER);
        AssertUtil.assertNotEmpty(gameRecords, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(gameRecords.size() == 1, ErrorCode.ERR_INTER);
        MariaMakilingGameRecordDo gameRecord = gameRecords.get(0);
        if (gameRecord.hasReward()) {
            rets.addAll(gameRecords);
        }
        if (gameRecord.hasRewardFreeSpins()) {
            List<MariaMakilingGameRecordDo> freeSpinsGameRecords = getGame().play(GamePlayDo.builder()
                    .gameType(betDo.getGameType())
                    .betType(betDo.getBetType())
                    .betSubType(betDo.getBetSubType())
                    .betAmount(betDo.getBetDetail().getBetAmount())
                    .freeSpinsFlag(true)
                    .build());

            MariaMakilingGameRecordDo recordDo = rets.get(0);
            recordDo.setFreeSpinIds(Optional.ofNullable(freeSpinsGameRecords)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(item -> item.getIndex())
                    .collect(Collectors.toList()));

            rets.addAll(freeSpinsGameRecords);
        }

        // 构建游戏记录明细
        // 构建投注记录
        Long rewardAmount = rets.stream().mapToLong(item -> item.getRewardResult().getTotalRewardAmount()).sum();
        long betRecordId = idGeneratorHelper.nextId();
        BetRecordDo betRecord = BetRecordBuilder.build(betDo, betRecordId, betDo.getBetDetail().getBetAmount(), rewardAmount, MariaMakilingBetRecordDetailDo.builder()
                .gameRecordList(rets)
                .build());
        Long id = betRecordService.add(betRecord);
        AssertUtil.assertNotNull(id, ErrorCode.ERR_INTER);
        // 单钱包投注并派彩 todo


        // 组装返回结果
        return BetResultDo.<MariaMakilingGameRecordDo>builder()
                .betRecordId(betRecord.getId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .resultDetail(rets.get(0))
                .build();
    }
}
