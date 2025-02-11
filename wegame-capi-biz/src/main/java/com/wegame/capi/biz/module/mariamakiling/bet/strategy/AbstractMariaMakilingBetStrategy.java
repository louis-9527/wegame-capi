package com.wegame.capi.biz.module.mariamakiling.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingBetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.manager.AbstractRecordManager;
import com.wegame.capi.biz.manager.MariaMakilingRecordManager;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractBetStrategy;
import com.wegame.capi.biz.module.base.game.Game;
import com.wegame.capi.biz.module.mariamakiling.game.MariaMakilingGame;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * MariaMakiling类游戏投注抽象接口
 */
public abstract class AbstractMariaMakilingBetStrategy extends AbstractBetStrategy<MariaMakilingGameRecordDo, MariaMakilingBetDo, MariaMakilingGameRecordDo>
        implements MariaMakilingBetStrategy<MariaMakilingBetDo, MariaMakilingGameRecordDo> {

    @Autowired
    MariaMakilingGame mariaMakilingGame;
    @Autowired
    MariaMakilingRecordManager mariaMakilingRecordManager;

    @Override
    public Game<MariaMakilingGameRecordDo> getGame() {
        return mariaMakilingGame;
    }

    @Override
    public AbstractRecordManager<MariaMakilingGameRecordDo> getRecordManager() {
        return mariaMakilingRecordManager;
    }

    @Override
    public BetResultDo<MariaMakilingGameRecordDo> bet(BetDo<MariaMakilingBetDo> betDo) {
        if (!BetSubTypeEnum.isFreeSpins(betDo.getBetSubType().getCode())) {
            // 非免费轮投注
            return betForNonFreeSpins(betDo);
        } else {
            // 免费轮投注, 走查询
            return betForFreeSpins(betDo);
        }
    }

    /**
     * 免费轮投注
     */
    private BetResultDo<MariaMakilingGameRecordDo> betForFreeSpins(BetDo<MariaMakilingBetDo> betDo) {
        MariaMakilingBetDo betDetail = betDo.getBetDetail();
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        List<MariaMakilingGameRecordDo> gameRecordList = mariaMakilingRecordManager.listGameRecordWithCache(GameRecordQuery.builder()
                        .betRecordId(betDo.getBetDetail().getBetRecordId())
                        .playerId(userInfo.getPlayerId())
                        .orderByClause("_id")
                        .direction(Sort.Direction.ASC)
                        .build(),
                getGame().getGameInfo().getGameType());
        // 匹配-免费轮游戏记录
        MariaMakilingGameRecordDo gameRecord = gameRecordList
                .stream()
                .filter(item -> Objects.equals(item.getId(), betDetail.getGameRecordId()))
                .findFirst()
                .orElse(null);
        AssertUtil.assertNotNull(gameRecord, ErrorCode.ERR_PARAM);
        // 统计-当前轮次及之前记录奖励金额之和
        Long overallRewardAmount = Optional.ofNullable(gameRecordList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(item -> item.getRewardResult() != null && item.getId() <= betDetail.getGameRecordId())
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        // 后处理-免费轮
        freeSpinsAfterHandle(betDo, gameRecordList, overallRewardAmount);
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
     */
    private BetResultDo<MariaMakilingGameRecordDo> betForNonFreeSpins(BetDo<MariaMakilingBetDo> betDo) {
        long betRecordId = generateRecordId();
        List<MariaMakilingGameRecordDo> list = getGame().play(GamePlayDo.builder()
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(betDo.getBetDetail().getBetAmount())
                .betRecordId(betRecordId)
                .build());
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        // 构建投注记录
        BetRecordDo<MariaMakilingGameRecordDo> betRecord = BetRecordBuilder.build(betRecordId,
                betDo,
                betDo.getBetDetail().getBetAmount(),
                betDo.getBetDetail().getPayAmount(),
                list.stream().filter(item -> Objects.nonNull(item.getRewardResult()))
                        .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                        .sum(),
                list);
        MariaMakilingGameRecordDo gameRecord = list.get(0);
        // 保存投注记录并派奖
        Long userCredit = saveBetRecordAndBetWithPayout(betRecord, gameRecord.hasRewardFreeSpins());
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


    public void freeSpinsAfterHandle(BetDo<MariaMakilingBetDo> betDo, List<MariaMakilingGameRecordDo> gameRecordList, Long totalRewardAmount) {
        // do nothing
    }

}
