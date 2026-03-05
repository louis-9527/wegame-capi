package com.wegame.capi.biz.module.diawatamayari.bet.strategy;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.BizInterfereConfig;
import com.wegame.capi.biz.config.domain.DiwataMayariConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.DiwataMayariBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.bizinterfere.BizInterfereContextDo;
import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.DiwataMayariBetRecordManager;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.DiwataMayariOperationEventManager;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractCommonBetStrategy;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.module.diawatamayari.bet.DiwataMayariBetHelper;
import com.wegame.capi.biz.module.diawatamayari.game.DiwataMayariGameContext;
import com.wegame.capi.biz.service.bizinterfere.BizInterfereService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.BizInterfereSubTypeEnum;
import com.wegame.capi.common.enums.BizInterfereTypeEnum;
import com.wegame.capi.dao.repository.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.GamePlayBuilder.build;

/**
 * DiwataMayari游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractDiwataMayariBetStrategy
        extends AbstractCommonBetStrategy<DiwataMayariGameRecordDo, DiwataMayariBetRecordDetailDo>
        implements DiwataMayariBetStrategy<DiwataMayariBetRecordDetailDo> {

    @Autowired
    DiwataMayariGameContext<DiwataMayariGameRecordDo> diwataMayariGameContext;
    @Autowired
    DiwataMayariBetRecordManager diwataMayariBetRecordManager;

    @Autowired
    @Qualifier("diwataMayariOperationEventManager")
    private DiwataMayariOperationEventManager diwataMayariOperationEventManager;
    @Autowired
    private DiwataMayariBetHelper diwataMayariBetHelper;
    @Autowired
    private DiwataMayariConfig diwataMayariConfig;
    @Autowired
    private BizInterfereService bizInterfereService;

    @Override
    public AbstractGameContext<DiwataMayariGameRecordDo> gameContext() {
        return diwataMayariGameContext;
    }


    @Override
    public AbstractBetRecordManager<DiwataMayariGameRecordDo> betRecordManager() {
        return diwataMayariBetRecordManager;
    }


    @Override
    public AbstractOperationEventManager operationEventManager() {
        return diwataMayariOperationEventManager;
    }

    @Override
    public BetHelper<DiwataMayariBetRecordDetailDo, DiwataMayariGameRecordDo> betHelper() {
        return diwataMayariBetHelper;
    }

    @Override
    public BetResultDo<DiwataMayariBetRecordDetailDo> bet(BetDo betDo) {
        if (!BetSubTypeEnum.isFreeWheelQuery(betDo.getBetSubType().getCode())) {
            // 非免费轮投注流程
            //// 1、是否使用星卡
            if (isCanStarCardBet(betDo)) {
                // 1.1 是，则星卡投注流程
                return betForStartCard(betDo);
            } else if (isCanFreeSpinBet(betDo)) {
                // 1.1 是，则预定投注流程
                return betForFreeSpin(betDo);
            } else {
                // 1.2 否，则走其他投注流程
                // 1.2.1、判断能否走干预事件投注
                if (isCanOperationEventBet(betDo)) {
                    // 1.2.11 命中干预事件：走干预事件投注
                    return betForOperationEvent(betDo);
                } else {
                    // 1.2.12 未命中干预事件：走正常投注。
                    return betForNonFreeWheel(betDo);
                }
            }
        } else {
            // 免费轮投注, 走查询
            return betForFreeWheelQuery(betDo);
        }
    }

    public GamePlayTypeEnum getGamePlayType() {
        return GamePlayTypeEnum.TEMPLATE_PLAY;
    }

    @Override
    protected long getTotalRewardAmount(List<DiwataMayariGameRecordDo> gameRecordList, Object extParam) {
        if (CollectionUtil.isEmpty(gameRecordList)) {
            return 0L;
        } else {
            return gameRecordList.stream()
                    .filter(item -> Objects.nonNull(item.getRewardResult()))
                    .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                    .sum();
        }
    }

    /**
     * 非免费轮投注(正常投注、购买投注)
     */
    private BetResultDo<DiwataMayariBetRecordDetailDo> betForNonFreeWheel(BetDo betDo) {
        // 业务干预上下文初始化
        betDo.setBizInterfereContext(BizInterfereContextDo.builder()
                .gameType(betDo.getGameType())
                .build());
        // 1、生成游戏记录
        long betRecordId = generateBetRecordId();
        BetRecordDo<DiwataMayariGameRecordDo> betRecord;
        List<DiwataMayariGameRecordDo> firstBatchList;
        do {
            List<DiwataMayariGameRecordDo> list = super.play(betDo, betRecordId, diwataMayariConfig.getPayoutAmountLimitConfig(), null);
            AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
            firstBatchList = getFirstBatchGameRecordList(list);
            // 2、构建投注记录
            betRecord = diwataMayariBetHelper.buildBetRecord(betRecordId, betDo, list);
            // bizInterfere 扣减
        } while (!this.bizInterfereDecyIfNeed(betDo, betRecord));

        SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betDo, betRecord, diwataMayariBetHelper.hasRewardFreeWheel(firstBatchList));
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.SUCCESS)) {
            // bizInterfere 抽成
            this.updateMultipleControl(betDo, betRecord);
        }
        // 投注成功后发送延迟派奖通知
        sendDelayRewardNotifyAfterBet(resultDo, betRecord);
        return diwataMayariBetHelper.handleBetResult(betRecord, resultDo);

    }

    private void updateMultipleControl(BetDo betDo, BetRecordDo<DiwataMayariGameRecordDo> betRecord) {
        BizInterfereContextDo bizInterfereContext = betDo.getBizInterfereContext();
        if (Objects.isNull(bizInterfereContext)) {
            return;
        }
        BizInterfereConfig bizInterfereConfig = diwataMayariConfig.getBizInterfereConfig();
        if (!bizInterfereConfig.isMultipleControlEnabled()) {
            return;
        }
        Map<BizInterfereSubTypeEnum, BizInterfereConfig.MultipleControlConfig.MultipleControlConfigMappingInfo> multipleControlConfigMap
                = bizInterfereConfig.getMultipleControlConfigMap();
        Long payAmount = betDo.getPayAmount();
        Map<Integer, Long> updateMultipleControlMap = new HashMap<>();
        multipleControlConfigMap.forEach((subType, mappingInfo) -> {
            BigDecimal betIntoRewardRatio = mappingInfo.getBetIntoRewardRatio();
            long incrAmount = betIntoRewardRatio.divide(BigDecimal.valueOf(100), 3, RoundingMode.FLOOR)
                    .multiply(BigDecimal.valueOf(payAmount)).longValue();
            updateMultipleControlMap.put(subType.getCode(), incrAmount);
        });
        bizInterfereService.asyncUpdateBizInterfere(betDo.getGameType(), BizInterfereTypeEnum.MULTIPLE_CONTROL, updateMultipleControlMap);
    }

    private boolean bizInterfereDecyIfNeed(BetDo betDo, BetRecordDo<DiwataMayariGameRecordDo> betRecord) {
        // 业务干预开关是否打开
        BizInterfereContextDo bizInterfereContext = betDo.getBizInterfereContext();
        if (Objects.isNull(bizInterfereContext)) {
            return true;
        }
        BizInterfereConfig bizInterfereConfig = diwataMayariConfig.getBizInterfereConfig();
        if (!bizInterfereConfig.isMultipleControlEnabled() || !bizInterfereContext.isBizInterfere()) {
            return true;
        }

        return bizInterfereService.decrOneReward(betDo.getGameType(),
                BizInterfereTypeEnum.MULTIPLE_CONTROL, bizInterfereContext.getInUseJackPotSnapshotType(), betRecord.getPayOutAmount());
    }

    /**
     * 免费轮投注
     */
    private BetResultDo<DiwataMayariBetRecordDetailDo> betForFreeWheelQuery(BetDo betDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        //        List<DiwataMayariGameRecordDo> gameRecordList = betRecordManager().listGameRecord(
        //                GameRecordQuery.builder()
        //                        .betRecordId(betDo.getBetRecordId())
        //                        .playerId(userInfo.getPlayerId())
        //                        .batchId(betDo.getGameRecordId())
        //                        .build(), gameContext().gameInfo().getGameType());
        GameRecordQuery query = GameRecordQuery.builder()
                .betRecordId(betDo.getBetRecordId())
                .cacheKey(String.format("%s_%s", betDo.getBetRecordId(), userInfo.getPlayerId()))
                .fromPrimary(true)
                .build();
        List<DiwataMayariGameRecordDo> allGameRecordList = betRecordManager().listGameRecordWithCache(query,
                gameContext().gameInfo().getGameType());
        // todo delete
        if (CollectionUtils.isEmpty(allGameRecordList)) {
            log.warn("allGameRecordList is null. cacheKey: {}", query.getCacheKey());
        }
        AssertUtil.assertNotEmpty(allGameRecordList, ErrorCode.ERR_PARAM);
        // 按照index升序排列
        List<DiwataMayariGameRecordDo> currentWheelGameRecordList = Optional.ofNullable(allGameRecordList).orElse(Collections.emptyList()).stream()
                .filter(item -> Objects.nonNull(item) && Objects.equals(item.getBatchId(), betDo.getGameRecordId()))
                .sorted(Comparator.comparingInt(DiwataMayariGameRecordDo::getIndex))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(currentWheelGameRecordList)) {
            log.warn("currentWheelGameRecordList is null. cacheKey: {}, betDo: {}, allGameRecordList: {}", query.getCacheKey(), betDo, allGameRecordList);
        }
        AssertUtil.assertNotEmpty(currentWheelGameRecordList, ErrorCode.ERR_PARAM);
        //// 合计中奖总金额
        long overallRewardAmount = currentWheelGameRecordList.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        if (CollectionUtils.isNotEmpty(currentWheelGameRecordList)) {
            DiwataMayariGameRecordDo lastGameRecord = currentWheelGameRecordList.stream().filter(item -> BooleanUtils.isTrue(item.isLastGameFlag())).findFirst().orElse(null);
            if (Objects.nonNull(lastGameRecord)) {
                long payOutAmount = allGameRecordList.stream()
                        .filter(item -> Objects.nonNull(item.getRewardResult()))
                        .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                        .sum();
                betRecordManager().asyncEvictGameRecordCache(query, gameContext().gameInfo().getGameType());
            }
        }
        return BetResultDo.<DiwataMayariBetRecordDetailDo>builder()
                .betRecordId(betDo.getBetRecordId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .overallRewardAmount(overallRewardAmount)
                .totalPayoutAmount(0L)
                .resultDetail(DiwataMayariBetRecordDetailDo.builder()
                        .allWheelRewardSubType(RewardHelper.calculateRewardSubType(betDo.getBetAmount(), overallRewardAmount))
                        .gameRecordList(currentWheelGameRecordList)
                        .build())
                .build();
    }

    private List<DiwataMayariGameRecordDo> getFirstBatchGameRecordList(List<DiwataMayariGameRecordDo> list) {
        Long batchId = list.get(0).getBatchId();
        return list.stream()
                .filter(item -> Objects.equals(batchId, item.getBatchId()))
                .collect(Collectors.toList());
    }

}
