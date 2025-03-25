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
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.manager.AbstractRecordManager;
import com.wegame.capi.biz.manager.MariaMakilingRecordManager;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.MariaMakilingOperationEventManager;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractBetPromotionStrategy;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.module.mariamakiling.game.MariaMakilingGameContext;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.base.AmountHelper.maxAmountControl;
import static com.wegame.capi.biz.module.base.AmountHelper.reachMaxMultiple;

/**
 * MariaMakiling类游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractMariaMakilingBetStrategy
        //        extends AbstractBetStrategy<MariaMakilingGameRecordDo, MariaMakilingGameRecordDo>
        //        implements MariaMakilingBetStrategy<MariaMakilingGameRecordDo> {
        extends AbstractBetPromotionStrategy<MariaMakilingGameRecordDo, MariaMakilingGameRecordDo, SymbolDo>
        implements MariaMakilingBetStrategy<MariaMakilingGameRecordDo> {

    @Autowired
    MariaMakilingGameContext mariaMakilingGameContext;
    @Autowired
    MariaMakilingRecordManager mariaMakilingRecordManager;
    @Autowired
    MariaMakilingConfig mariaMakilingConfig;
    @Autowired
    @Qualifier("mariaMakilingOperationEventManager")
    MariaMakilingOperationEventManager mariaMakilingOperationEventManager;

    @Override
    public AbstractGameContext<MariaMakilingGameRecordDo> gameContext() {
        return mariaMakilingGameContext;
    }

    @Override
    public AbstractRecordManager<MariaMakilingGameRecordDo> getRecordManager() {
        return mariaMakilingRecordManager;
    }

    @Override
    public AbstractOperationEventManager<SymbolDo> getDynamicOperationManager() {
        return mariaMakilingOperationEventManager;
    }
    //    public BetResultDo<MariaMakilingGameRecordDo> bet(BetDo betDo) {
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
    public BetResultDo<MariaMakilingGameRecordDo> betForFreeWheel(BetDo betDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        List<MariaMakilingGameRecordDo> gameRecordList = mariaMakilingRecordManager.listGameRecordWithCache(GameRecordQuery.builder()
                        .betRecordId(betDo.getBetRecordId())
                        .cacheKey(String.format("%s_%s", betDo.getBetRecordId(), userInfo.getPlayerId()))
                        .fromPrimary(true)
                        .build(),
                gameContext().gameInfo().getGameType());
        // 按照index升序排列
        gameRecordList = Optional.ofNullable(gameRecordList).orElse(Collections.emptyList()).stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(MariaMakilingGameRecordDo::getIndex))
                .collect(Collectors.toList());
        // 匹配-免费轮游戏记录 todo 可直接查询游戏记录
        MariaMakilingGameRecordDo matchGameRecord = gameRecordList
                .stream()
                .filter(item -> Objects.equals(item.getId(), betDo.getGameRecordId()))
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
                .betRecordId(betDo.getBetRecordId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .overallRewardAmount(matchGameRecord.getOverallRewardAmount())
                .resultDetail(matchGameRecord)
                .build();
    }

    @Override
    public boolean hasRewardFreeWheel(List<MariaMakilingGameRecordDo> gameRecordList) {
        RewardResultDo<SymbolDo> rewardResult = gameRecordList.get(0).getRewardResult();
        return Objects.nonNull(rewardResult) && CollectionUtils.isNotEmpty(rewardResult.getRewardLineDetailList()) && rewardResult.isHasRewardFreeWheel();
    }

    @Override
    public BetRecordDo<MariaMakilingGameRecordDo> buildBetRecord(Long betRecordId, BetDo betDo, List<MariaMakilingGameRecordDo> list) {
        // 构建投注记录
        long totalRewardAmount = list.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        return BetRecordBuilder.build(betRecordId,
                betDo,
                maxAmountControl(totalRewardAmount, betDo.getBetAmount(), mariaMakilingConfig.getRewardMaxMultiple()),
                reachMaxMultiple(totalRewardAmount, betDo.getBetAmount(), mariaMakilingConfig.getRewardMaxMultiple()),
                list);
    }

    @Override
    public BetResultDo<MariaMakilingGameRecordDo> buildResult(BetRecordDo<MariaMakilingGameRecordDo> betRecord, SingleWalletBetResultDo resultDo) {
        MariaMakilingGameRecordDo gameRecord = betRecord.getGameRecordList().get(0);
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.SUCCESS)) {
            return BetResultDo.<MariaMakilingGameRecordDo>builder()
                    .betRecordId(betRecord.getId())
                    .gameId(betRecord.getGameId())
                    .betType(BetTypeEnum.valueOfCode(betRecord.getBetType()))
                    .betSubType(BetSubTypeEnum.valueOfCode(betRecord.getBetSubType()))
                    .credit(resultDo.getCredit())
                    .overallRewardAmount(gameRecord.getOverallRewardAmount())
                    .totalPayoutAmount(betRecord.getPayOutAmount())
                    .resultDetail(gameRecord)
                    .build();
        } else if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.FAIL)) {
            if (resultDo.getErrorCode() != null) {
                throw new BusinessException(resultDo.getErrorCode());
            }
            throw new BusinessException(ErrorCode.ERR_INTER);
        } else if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.ING)) {
            throw new BusinessException(ErrorCode.ERR_NET);
        } else {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
    }

    public void freeWheelAfterHandle(BetDo betDo, MariaMakilingBetRecordDetailDo detailDo, Long totalRewardAmount) {
        // do nothing
    }
}
