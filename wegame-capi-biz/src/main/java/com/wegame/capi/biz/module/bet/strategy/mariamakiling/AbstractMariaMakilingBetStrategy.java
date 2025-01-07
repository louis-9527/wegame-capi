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
        // 首次游戏结果
        MariaMakilingGameRecordDo gameRecord = this.gameRecordFirst(betDo);
        List<MariaMakilingGameRecordDo> gameRecordDos = new ArrayList<>();
        gameRecordDos.add(gameRecord);
        // 判断是否中免费轮
        if (gameRecord.hasRewardFreeSpins()) {
            List<MariaMakilingGameRecordDo> gameRecordFreeSpins = this.gameRecordFreeSpins(betDo);
            // 组装免费轮IDS
            gameRecord.setFreeSpinIds(Optional.ofNullable(gameRecordFreeSpins)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(MariaMakilingGameRecordDo::getIndex)
                    .collect(Collectors.toList()));
            gameRecordDos.addAll(gameRecordFreeSpins);
        }

        // 构建投注记录(正常轮和免费轮合并)
        Long rewardAmount = gameRecordDos.stream().filter(item -> item.getRewardResult() != null).mapToLong(item -> item.getRewardResult().getTotalRewardAmount()).sum();
        BetRecordDo betRecord = BetRecordBuilder.build(betDo, getRecordId(), betDo.getBetDetail().getBetAmount(), rewardAmount, MariaMakilingBetRecordDetailDo.builder()
                .gameRecordList(gameRecordDos)
                .build());

        // 保存投注记录并派奖
        super.saveBetRecordAndPayOut(betRecord);

        // 组装返回结果
        return BetResultDo.<MariaMakilingGameRecordDo>builder()
                .betRecordId(betRecord.getId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .resultDetail(gameRecord)
                .build();
    }



    /**
     * 获取首次游戏结果
     *
     * @param betDo
     * @return
     */
    private MariaMakilingGameRecordDo gameRecordFirst(BetDo<MariaMakilingBetDo> betDo) {
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
        return gameRecords.get(0);
    }

    /**
     * 获取免费轮游戏结果
     *
     * @return
     */
    private List<MariaMakilingGameRecordDo> gameRecordFreeSpins(BetDo<MariaMakilingBetDo> betDo) {
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
