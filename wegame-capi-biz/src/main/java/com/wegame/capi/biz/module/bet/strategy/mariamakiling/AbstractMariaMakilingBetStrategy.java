package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MariaMakilingBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingBetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.module.bet.strategy.AbstractBetStrategy;
import com.wegame.capi.biz.module.game.Game;
import com.wegame.capi.biz.module.game.MariaMakilingGame;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractMariaMakilingBetStrategy extends AbstractBetStrategy<MariaMakilingBetDo, MariaMakilingGameRecordDo> {

    @Autowired
    MariaMakilingGame mariaMakilingGame;

    @Override
    public Game<MariaMakilingGameRecordDo> getGame() {
        return mariaMakilingGame;
    }

    @Override
    public BetResultDo<MariaMakilingGameRecordDo> bet(BetDo<MariaMakilingBetDo> betDo) {
        if (BetSubTypeEnum.isFreeSpins(betDo.getBetSubType().getCode())) {
            // 免费轮投注
            return betForFreeSpins(betDo);
        } else {
            // 非免费轮投注
            return betForNonFreeSpins(betDo);
        }
    }

    private BetResultDo<MariaMakilingGameRecordDo> betForFreeSpins(BetDo<MariaMakilingBetDo> betDo) {
        MariaMakilingBetDo betDetail = betDo.getBetDetail();
        BetRecordDo<MariaMakilingBetRecordDetailDo> betRecordDo = betRecordService.get(betDetail.getBetRecordId());
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);
        MariaMakilingBetRecordDetailDo betRecordDetailDo = betRecordDo.getDetail();
        AssertUtil.assertNotNull(betRecordDetailDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotEmpty(betRecordDetailDo.getGameRecordList(), ErrorCode.ERR_PARAM);
        List<MariaMakilingGameRecordDo> gameRecordList = betRecordDetailDo.getGameRecordList();
        // 匹配-免费轮游戏记录
        MariaMakilingGameRecordDo gameRecord = gameRecordList
                .stream()
                .filter(item -> Objects.equals(item.getIndex(), betDetail.getFreeSpinsId()))
                .findFirst()
                .orElse(null);
        // 统计-当前轮次及之前记录奖励金额之和
        Long overallRewardAmount = Optional.ofNullable(gameRecordList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(item -> item.getRewardResult() != null && item.getIndex() <= betDetail.getFreeSpinsId())
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        // 后处理-免费轮
        freeSpinsAfterHandle(betDo, betRecordDetailDo, overallRewardAmount);
        // 返回
        return BetResultDo.<MariaMakilingGameRecordDo>builder()
                .betRecordId(betDetail.getBetRecordId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .overallRewardAmount(overallRewardAmount)
                .resultDetail(gameRecord)
                .build();
    }

    private BetResultDo<MariaMakilingGameRecordDo> betForNonFreeSpins(BetDo<MariaMakilingBetDo> betDo) {
        List<MariaMakilingGameRecordDo> gameRecordList = new ArrayList<>();
        MariaMakilingGameRecordDo firstGameRecord = firstPlayGame(betDo);
        gameRecordList.add(firstGameRecord);
        // 判断-是否中免费轮
        if (firstGameRecord.hasRewardFreeSpins()) {
            List<MariaMakilingGameRecordDo> freeSpinsGameRecordList = freeSpinsPlayGame(betDo);
            // 组装-免费轮IDS
            firstGameRecord.setFreeSpinIds(Optional.ofNullable(freeSpinsGameRecordList)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(MariaMakilingGameRecordDo::getIndex)
                    .collect(Collectors.toList()));
            gameRecordList.addAll(freeSpinsGameRecordList);
        }
        // 构建-投注记录
        Long totalRewardAmount = gameRecordList.stream()
                .filter(item -> item.getRewardResult() != null)
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        MariaMakilingBetRecordDetailDo detailDo = MariaMakilingBetRecordDetailDo.builder()
                .gameRecordList(gameRecordList)
                .build();
        BetRecordDo betRecord = BetRecordBuilder.build(generateRecordId(),
                betDo,
                betDo.getBetDetail().getBetAmount(),
                betDo.getBetDetail().getPayAmount(),
                totalRewardAmount,
                detailDo);
        // 保存投注记录并派奖
        Long userCredit = saveBetRecordAndBetWithPayout(betRecord);
        // 组装返回结果
        return BetResultDo.<MariaMakilingGameRecordDo>builder()
                .betRecordId(betRecord.getId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .credit(userCredit)
                .overallRewardAmount(Objects.nonNull(firstGameRecord.getRewardResult()) ? firstGameRecord.getRewardResult().getTotalRewardAmount() : 0L)
                .resultDetail(firstGameRecord)
                .build();
    }

    private MariaMakilingGameRecordDo firstPlayGame(BetDo<MariaMakilingBetDo> betDo) {
        List<MariaMakilingGameRecordDo> gameRecords = getGame().play(GamePlayDo.builder()
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(betDo.getBetDetail().getBetAmount())
                .freeSpinsFlag(false)
                .build());
        AssertUtil.assertNotEmpty(gameRecords, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(gameRecords.size() == 1, ErrorCode.ERR_INTER);
        return gameRecords.get(0);
    }

    private List<MariaMakilingGameRecordDo> freeSpinsPlayGame(BetDo<MariaMakilingBetDo> betDo) {
        return getGame().play(GamePlayDo.builder()
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(betDo.getBetDetail().getBetAmount())
                .freeSpinsFlag(true)
                .build());
    }


    public void freeSpinsAfterHandle(BetDo<MariaMakilingBetDo> betDo, MariaMakilingBetRecordDetailDo recordDetail, Long totalRewardAmount) {
        // do nothing
    }

}
