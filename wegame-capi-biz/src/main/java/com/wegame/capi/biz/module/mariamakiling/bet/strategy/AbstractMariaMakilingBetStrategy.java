package com.wegame.capi.biz.module.mariamakiling.bet.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
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
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.manager.AbstractRecordManager;
import com.wegame.capi.biz.manager.MariaMakilingRecordManager;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractBetStrategy;
import com.wegame.capi.biz.module.base.bet.strategy.DynamicOperationBetStrategy;
import com.wegame.capi.biz.module.base.game.Game;
import com.wegame.capi.biz.module.mariamakiling.game.MariaMakilingGame;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.DynamicOperationEventKindEnum;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import javafx.util.Pair;
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
        extends DynamicOperationBetStrategy<MariaMakilingGameRecordDo, MariaMakilingBetDo, MariaMakilingGameRecordDo, SymbolDo>
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

//    @Override
//    public BetResultDo<MariaMakilingGameRecordDo> bet(BetDo<MariaMakilingBetDo> betDo) {
//        if (!BetSubTypeEnum.isFreeWheel(betDo.getBetSubType().getCode())) {
//            // 非免费轮投注
//            return betForNonFreeWheel(betDo);
//        } else {
//            // 免费轮投注, 走查询
//            return betForFreeWheel(betDo);
//        }
//    }


    /**
     * 免费轮投注
     */
    @Override
    public BetResultDo<MariaMakilingGameRecordDo> betForFreeWheel(BetDo<MariaMakilingBetDo> betDo) {
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

    @Override
    public Pair<Long, Long> getBetAndPayAmount(BetDo<MariaMakilingBetDo> betDo) {
        return new Pair<>(betDo.getBetDetail().getBetAmount(), betDo.getBetDetail().getPayAmount());
    }
    @Override
    public DynamicOperationEventKindEnum eventKindEnum(BetTypeEnum betType) {
        if (BetTypeEnum.NORMAL_BET == betType) {
            return DynamicOperationEventKindEnum.NORMAL_URGENT;
        }
        if (BetTypeEnum.BUY_BET == betType) {
            return DynamicOperationEventKindEnum.FREE_URGENT;
        }
        return null;
    }

    @Override
    public BetRecordDo<MariaMakilingGameRecordDo> buildBetRecord(Long betRecordId, BetDo<MariaMakilingBetDo> betDo, List<MariaMakilingGameRecordDo> list) {
        // 构建投注记录
        long totalRewardAmount = list.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        return BetRecordBuilder.build(betRecordId,
                betDo,
                betDo.getBetDetail().getBetAmount(),
                betDo.getBetDetail().getPayAmount(),
                maxAmountControl(totalRewardAmount, betDo.getBetDetail().getPayAmount(), mariaMakilingConfig.getRewardMaxMultiple()),
                reachMaxMultiple(totalRewardAmount, betDo.getBetDetail().getPayAmount(), mariaMakilingConfig.getRewardMaxMultiple()),
                list);
    }

    @Override
    public BetResultDo<MariaMakilingGameRecordDo> saveBetRecord(BetRecordDo<MariaMakilingGameRecordDo> betRecord) {
        MariaMakilingGameRecordDo gameRecord = betRecord.getGameRecordList().get(0);
        // 保存投注记录并派奖
        BetSingleWalletResultDo betSingleWalletResult = saveBetRecordAndBetWithPayout(betRecord, gameRecord.hasRewardFreeSpins());
        // 组装返回结果
        return BetResultDo.<MariaMakilingGameRecordDo>builder()
                .betRecordId(betRecord.getId())
                .gameId(betRecord.getGameId())
                .betType(BetTypeEnum.valueOfCode(betRecord.getBetType()))
                .betSubType(BetSubTypeEnum.valueOfCode(betRecord.getBetSubType()))
                .credit(AmountUtil.toLong(betSingleWalletResult.getCredit()))
                .overallRewardAmount(gameRecord.getOverallRewardAmount())
                .totalPayoutAmount(betRecord.getPayOutAmount())
                .resultDetail(gameRecord)
                .betId(betSingleWalletResult.getBetId())
                .build();
    }

    @Override
    public List<WheelDo<SymbolDo>> convertWheelDoList(List<Object> wheelConfig) {
        return JSON.parseObject(JSON.toJSONString(wheelConfig), new TypeReference<List<WheelDo<SymbolDo>>>() {
        });
    }

    public void freeWheelAfterHandle(BetDo<MariaMakilingBetDo> betDo, MariaMakilingBetRecordDetailDo detailDo, Long totalRewardAmount) {
        // do nothing
    }

}
