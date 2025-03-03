package com.wegame.capi.biz.module.mariamakiling.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MariaMakilingBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.BetSingleWalletResultDo;
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
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.base.AmountUtil.maxAmountControl;
import static com.wegame.capi.biz.module.base.AmountUtil.reachMaxMultiple;

/**
 * MariaMakiling类游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractMariaMakilingBetStrategy
        extends AbstractBetStrategy<MariaMakilingGameRecordDo, MariaMakilingBetDo, MariaMakilingGameRecordDo>
        implements MariaMakilingBetStrategy<MariaMakilingBetDo, MariaMakilingGameRecordDo> {

    @Autowired
    MariaMakilingGame mariaMakilingGame;
    @Autowired
    MariaMakilingRecordManager mariaMakilingRecordManager;
    @Autowired
    MariaMakilingConfig mariaMakilingConfig;

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
        if (!BetSubTypeEnum.isFreeWheel(betDo.getBetSubType().getCode())) {
            // 非免费轮投注
            return betForNonFreeWheel(betDo);
        } else {
            // 免费轮投注, 走查询
            return betForFreeWheel(betDo);
        }
    }

    /**
     * 免费轮投注
     */
    private BetResultDo<MariaMakilingGameRecordDo> betForFreeWheel(BetDo<MariaMakilingBetDo> betDo) {
        MariaMakilingBetDo betDetail = betDo.getBetDetail();
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        List<MariaMakilingGameRecordDo> gameRecordList = mariaMakilingRecordManager.listGameRecordWithCache(GameRecordQuery.builder()
                        .betRecordId(betDo.getBetDetail().getBetRecordId())
                        .cacheKey(String.format("%s_%s", betDo.getBetDetail().getBetRecordId(), userInfo.getPlayerId()))
                        .build(),
                getGame().getGameInfo().getGameType());
        // 按照index升序排列
        gameRecordList = Optional.ofNullable(gameRecordList).orElse(Collections.emptyList()).stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(MariaMakilingGameRecordDo::getIndex))
                .collect(Collectors.toList());
        // 匹配-免费轮游戏记录 todo 可直接查询游戏记录
        MariaMakilingGameRecordDo matchGameRecord = gameRecordList
                .stream()
                .filter(item -> Objects.equals(item.getId(), betDetail.getGameRecordId()))
                .findFirst()
                .orElse(null);
        AssertUtil.assertNotNull(matchGameRecord, ErrorCode.ERR_PARAM, "NOT FOUND GAME RECORD");
        // 后处理-免费轮
        MariaMakilingBetRecordDetailDo detailDo = MariaMakilingBetRecordDetailDo.builder()
                .gameRecordList(gameRecordList)
                .build();
        freeWheelAfterHandle(betDo, detailDo, matchGameRecord.getOverallRewardAmount());
        // 返回
        return BetResultDo.<MariaMakilingGameRecordDo>builder()
                .betRecordId(betDetail.getBetRecordId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .overallRewardAmount(matchGameRecord.getOverallRewardAmount())
                .resultDetail(matchGameRecord)
                .build();
    }

    /**
     * 非免费轮投注
     */
    private BetResultDo<MariaMakilingGameRecordDo> betForNonFreeWheel(BetDo<MariaMakilingBetDo> betDo) {
        long betRecordId = generateBetRecordId();
        List<MariaMakilingGameRecordDo> list = getGame().play(GamePlayDo.builder()
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(betDo.getBetDetail().getBetAmount())
                .payAmount(betDo.getBetDetail().getPayAmount())
                .betRecordId(betRecordId)
                .build());
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        // 构建投注记录
        //// 总中奖金额
        long totalRewardAmount = list.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        BetRecordDo<MariaMakilingGameRecordDo> betRecord = BetRecordBuilder.build(betRecordId,
                betDo,
                betDo.getBetDetail().getBetAmount(),
                betDo.getBetDetail().getPayAmount(),
                maxAmountControl(totalRewardAmount, betDo.getBetDetail().getPayAmount(), mariaMakilingConfig.getRewardMaxMultiple()),
                reachMaxMultiple(totalRewardAmount, betDo.getBetDetail().getPayAmount(), mariaMakilingConfig.getRewardMaxMultiple()),
                list);
        // 保存投注记录并派奖
        MariaMakilingGameRecordDo gameRecord = list.get(0);
        BetSingleWalletResultDo resultDo = saveBetRecordAndBetWithPayout(betRecord, gameRecord.hasRewardFreeSpins());
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.SUCCESS)) {
            return BetResultDo.<MariaMakilingGameRecordDo>builder()
                    .betRecordId(betRecord.getId())
                    .gameId(betDo.getGameId())
                    .betType(betDo.getBetType())
                    .betSubType(betDo.getBetSubType())
                    .credit(resultDo.getCredit().longValue())
                    .overallRewardAmount(gameRecord.getOverallRewardAmount())
                    .totalPayoutAmount(betRecord.getPayOutAmount())
                    .resultDetail(gameRecord)
                    .build();
        } else if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.FAIL)) {
            throw new BusinessException(ErrorCode.ERR_INTER, resultDo.getReason());
        } else if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.ING)) {
            throw new BusinessException(ErrorCode.ERR_NET);
        } else {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
    }

    public void freeWheelAfterHandle(BetDo<MariaMakilingBetDo> betDo, MariaMakilingBetRecordDetailDo detailDo, Long totalRewardAmount) {
        // do nothing
    }

}
