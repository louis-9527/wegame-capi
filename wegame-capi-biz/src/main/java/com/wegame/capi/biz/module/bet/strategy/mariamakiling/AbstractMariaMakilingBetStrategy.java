package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.google.common.collect.Lists;
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
        // 是否查询免费轮详情
        if (betDo.getBetSubType() == BetSubTypeEnum.FREE_SPINS_BUY_BET ||
                betDo.getBetSubType() == BetSubTypeEnum.FREE_SPINS_NORMAL_BET) {
            MariaMakilingBetDo betDetail = betDo.getBetDetail();
            MariaMakilingBetRecordDetailDo resultDetail = this.queryGameRecord(betDetail.getBetRecordId(), betDetail.getFreeSpinsId());
            // 免费轮子类处理逻辑
            this.freeSpinsHandler(betDo, resultDetail);
            // 返回免费轮详情
            return BetResultDo.<MariaMakilingGameRecordDo>builder()
                    .betRecordId(betDetail.getBetRecordId())
                    .gameId(betDo.getGameId())
                    .betType(betDo.getBetType())
                    .betSubType(betDo.getBetSubType())
                    .resultDetail(resultDetail.getGameRecordList().get(0))
                    .build();

        }
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
        long betRecordId = idGeneratorHelper.nextId();
        BetRecordDo betRecord = BetRecordBuilder.build(betDo, betRecordId, betDo.getBetDetail().getBetAmount(), rewardAmount, MariaMakilingBetRecordDetailDo.builder()
                .gameRecordList(gameRecordDos)
                .build());
        Long id = betRecordService.add(betRecord);
        AssertUtil.assertNotNull(id, ErrorCode.ERR_INTER);
        // 单钱包投注并派彩 todo
        // 修改投注状态 todo
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

    private MariaMakilingBetRecordDetailDo queryGameRecord(Long betRecordId, Integer freeSpinsId) {
        BetRecordDo<MariaMakilingBetRecordDetailDo> betRecordDo = betRecordService.get(betRecordId);
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);
        MariaMakilingBetRecordDetailDo betRecordDetailDo = betRecordDo.getDetail();
        AssertUtil.assertNotNull(betRecordDetailDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotEmpty(betRecordDetailDo.getGameRecordList(), ErrorCode.ERR_PARAM);
        List<MariaMakilingGameRecordDo> gameRecordList = betRecordDetailDo.getGameRecordList();
        // 匹配结果
        MariaMakilingGameRecordDo mariaMakilingGameRecordDo = gameRecordList
                .stream()
                .filter(item -> Objects.equals(item.getIndex(), freeSpinsId))
                .findFirst()
                .orElse(null);
        AssertUtil.assertNotNull(mariaMakilingGameRecordDo, ErrorCode.ERR_PARAM);
        // 如果最后一次查询则，返回总的中奖金额
        MariaMakilingBetRecordDetailDo resultDetail = MariaMakilingBetRecordDetailDo.builder()
                .gameRecordList(Lists.newArrayList(mariaMakilingGameRecordDo)).build();
        if (betRecordDetailDo.isLastFreeSpins(freeSpinsId)) {
            resultDetail.setPayOutTotalAmount(betRecordDo.getPayOutAmount());
        }
        return resultDetail;
    }

    public void freeSpinsHandler(BetDo<MariaMakilingBetDo> betDo, MariaMakilingBetRecordDetailDo recordDetail) {
    }

}
