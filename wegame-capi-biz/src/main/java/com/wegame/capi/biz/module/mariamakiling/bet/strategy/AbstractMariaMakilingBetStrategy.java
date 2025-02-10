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
import com.wegame.capi.biz.manager.RecordManager;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractBetStrategy;
import com.wegame.capi.biz.module.base.game.Game;
import com.wegame.capi.biz.module.mariamakiling.game.MariaMakilingGame;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * MariaMakiling类游戏投注抽象接口
 */
public abstract class AbstractMariaMakilingBetStrategy extends AbstractBetStrategy<MariaMakilingBetDo, MariaMakilingGameRecordDo>
        implements MariaMakilingBetStrategy<MariaMakilingBetDo, MariaMakilingGameRecordDo> {

    @Autowired
    MariaMakilingGame mariaMakilingGame;
    @Autowired
    RecordManager<MariaMakilingGameRecordDo> recordManager;
    @Autowired
    GameRecordService<MariaMakilingGameRecordDo> gameRecordService;

    @Override
    public Game<MariaMakilingGameRecordDo> getGame() {
        return mariaMakilingGame;
    }

    @Override
    public RecordManager<MariaMakilingGameRecordDo> getRecordManager() {
        return recordManager;
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
        // todo
        MariaMakilingBetDo betDetail = betDo.getBetDetail();
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        // todo 从GameRecord查询 todo
        List<MariaMakilingGameRecordDo> gameRecordList = gameRecordService.list(GameRecordQuery.builder()
                        .betRecordId(betDo.getBetDetail().getBetRecordId())
                        .playerId(userInfo.getPlayerId())
                        .orderByClause("_id")
                        .build(),
                getGame().getGameInfo().getGameType(),
                MariaMakilingGameRecordDo.class);
        // 匹配-免费轮游戏记录
        MariaMakilingGameRecordDo gameRecord = gameRecordList
                .stream()
                .filter(item -> Objects.equals(item.getId(), betDetail.getGameRecordId()))
                .findFirst()
                .orElse(null);
        // 统计-当前轮次及之前记录奖励金额之和
        Long overallRewardAmount = Optional.ofNullable(gameRecordList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(item -> item.getRewardResult() != null && item.getId() <= betDetail.getGameRecordId())
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        // 后处理-免费轮
        freeSpinsAfterHandle(betDo, MariaMakilingBetRecordDetailDo.builder().gameRecordList(gameRecordList).build(), overallRewardAmount);
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
        List<MariaMakilingGameRecordDo> list = getGame().play(GamePlayDo.builder()
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(betDo.getBetDetail().getBetAmount())
                .build());
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        //        // 判断-是否中免费轮
        //        if (gameRecord.hasRewardFreeSpins()) {
        //            // 执行免费轮投注
        //            List<MariaMakilingGameRecordDo> gameRecordList = freeSpinsPlayGame(betDo);
        //            // 组装-免费轮IDS
        //            gameRecord.setFreeSpinIds(Optional.ofNullable(gameRecordList)
        //                    .orElse(Collections.emptyList())
        //                    .stream()
        //                    .map(MariaMakilingGameRecordDo::getIndex)
        //                    .collect(Collectors.toList()));
        //            list.addAll(gameRecordList);
        //        }
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
        MariaMakilingGameRecordDo gameRecord = list.get(0);
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


    public void freeSpinsAfterHandle(BetDo<MariaMakilingBetDo> betDo, MariaMakilingBetRecordDetailDo recordDetail, Long totalRewardAmount) {
        // do nothing
    }

}
