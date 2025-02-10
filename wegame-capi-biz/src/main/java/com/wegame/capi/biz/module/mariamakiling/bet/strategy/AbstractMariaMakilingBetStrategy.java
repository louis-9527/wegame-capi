package com.wegame.capi.biz.module.mariamakiling.bet.strategy;

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
import com.wegame.capi.biz.module.base.bet.strategy.AbstractBetStrategy;
import com.wegame.capi.biz.module.base.game.Game;
import com.wegame.capi.biz.module.mariamakiling.game.MariaMakilingGame;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MariaMakiling类游戏投注抽象接口
 */
public abstract class AbstractMariaMakilingBetStrategy extends AbstractBetStrategy<MariaMakilingBetDo, MariaMakilingGameRecordDo>
        implements MariaMakilingBetStrategy<MariaMakilingBetDo, MariaMakilingGameRecordDo> {

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

    /**
     * 免费轮投注
     *
     * @param betDo
     * @return
     */
    private BetResultDo<MariaMakilingGameRecordDo> betForFreeSpins(BetDo<MariaMakilingBetDo> betDo) {
        MariaMakilingBetDo betDetail = betDo.getBetDetail();
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        BetRecordDo<MariaMakilingBetRecordDetailDo> betRecordDo = betRecordService.get(betDetail.getBetRecordId(), userInfo.getPlayerId(), getGame().getGameInfo().getGameType());
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

    /**
     * 非免费轮投注
     *
     * @param betDo
     * @return
     */
    private BetResultDo<MariaMakilingGameRecordDo> betForNonFreeSpins(BetDo<MariaMakilingBetDo> betDo) {
        List<MariaMakilingGameRecordDo> list = new ArrayList<>();
        MariaMakilingGameRecordDo gameRecord = playGame(betDo);
        list.add(gameRecord);
        // 判断-是否中免费轮
        if (gameRecord.hasRewardFreeSpins()) {
            // 执行免费轮投注
            List<MariaMakilingGameRecordDo> gameRecordList = freeSpinsPlayGame(betDo);
            // 组装-免费轮IDS
            gameRecord.setFreeSpinIds(Optional.ofNullable(gameRecordList)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(MariaMakilingGameRecordDo::getIndex)
                    .collect(Collectors.toList()));
            list.addAll(gameRecordList);
        }
        // 构建投注记录
        BetRecordDo<MariaMakilingBetRecordDetailDo> betRecord = BetRecordBuilder.build(generateRecordId(),
                betDo,
                betDo.getBetDetail().getBetAmount(),
                betDo.getBetDetail().getPayAmount(),
                list.stream().filter(item -> item.getRewardResult() != null)
                        .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                        .sum(),
                MariaMakilingBetRecordDetailDo.builder()
                        .gameRecordList(list)
                        .build());
        // 保存投注记录并派奖
        Long userCredit = saveBetRecordAndBetWithPayoutAsync(betRecord, gameRecord.hasRewardFreeSpins());
        // 组装返回结果
        return BetResultDo.<MariaMakilingGameRecordDo>builder()
                .betRecordId(betRecord.getId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .credit(userCredit)
                .overallRewardAmount(Objects.nonNull(gameRecord.getRewardResult()) ? gameRecord.getRewardResult().getTotalRewardAmount() : 0L)
                .resultDetail(gameRecord)
                .build();
    }

    private MariaMakilingGameRecordDo playGame(BetDo<MariaMakilingBetDo> betDo) {
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

    /**
     * 免费轮
     *
     * @param betDo
     * @return
     */
    private List<MariaMakilingGameRecordDo> freeSpinsPlayGame(BetDo<MariaMakilingBetDo> betDo) {
        return getGame().play(GamePlayDo.builder()
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(BetSubTypeEnum.valueOfFreeSpinsBetSubType(betDo.getBetType()))
                .betAmount(betDo.getBetDetail().getBetAmount())
                .freeSpinsFlag(true)
                .build());
    }


    public void freeSpinsAfterHandle(BetDo<MariaMakilingBetDo> betDo, MariaMakilingBetRecordDetailDo recordDetail, Long totalRewardAmount) {
        // do nothing
    }

}
