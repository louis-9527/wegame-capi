package com.wegame.capi.biz.module.mangkukulam.bet.strategy;

import com.wegame.base.Constants;
import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.MangkukulamConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MangkukulamBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.manager.AbstractBetRecordManager;
import com.wegame.capi.biz.manager.MangkukulamBetRecordManager;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.MangkukulamOperationEventManager;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.biz.module.base.bet.strategy.AbstractCommonBetStrategy;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.module.mangkukulam.MangkukulamCalcUtil;
import com.wegame.capi.biz.module.mangkukulam.bet.MangkukulamBetHelper;
import com.wegame.capi.biz.module.mangkukulam.game.MangkukulamGameContext;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.FreeWheelHandleStatusEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.VolatilityTypeEnum;
import com.wegame.capi.dao.repository.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.GamePlayBuilder.build;

/**
 * Mangkukulam游戏投注抽象接口
 */
@Slf4j
public abstract class AbstractMangkukulamBetStrategy extends AbstractCommonBetStrategy<MangkukulamGameRecordDo,
        MangkukulamBetRecordDetailDo> implements MangkukulamBetStrategy<MangkukulamBetRecordDetailDo> {

    @Autowired
    MangkukulamGameContext<MangkukulamGameRecordDo> mangkukulamGameContext;
    @Autowired
    MangkukulamBetRecordManager mangkukulamBetRecordManager;
    @Autowired
    GameRecordService<MangkukulamGameRecordDo> gameRecordService;
    @Autowired
    @Qualifier("mangkukulamOperationEventManager")
    private MangkukulamOperationEventManager mangkukulamOperationEventManager;

    @Autowired
    private MangkukulamBetHelper mangkukulamBetHelper;

    @Autowired
    private MangkukulamConfig mangkukulamConfig;

    @Override
    public AbstractGameContext<MangkukulamGameRecordDo> gameContext() {
        return mangkukulamGameContext;
    }

    @Override
    public AbstractBetRecordManager<MangkukulamGameRecordDo> betRecordManager() {
        return mangkukulamBetRecordManager;
    }

    @Override
    public AbstractOperationEventManager operationEventManager() {
        return mangkukulamOperationEventManager;
    }

    @Override
    public BetHelper<MangkukulamBetRecordDetailDo, MangkukulamGameRecordDo> betHelper() {
        return mangkukulamBetHelper;
    }

    @Override
    public BetResultDo<MangkukulamBetRecordDetailDo> bet(BetDo betDo) {
        if (!BetSubTypeEnum.isFreeWheelQuery(betDo.getBetSubType().getCode())) {
            // 非免费轮投注流程
            // 如果是第二步直接走betForNonFreeWheel
            if (BetSubTypeEnum.isSecondStep(betDo.getBetSubType().getCode())) {
                return betForNonFreeWheel(betDo);
            }
            //// 1、是否使用星卡
            if (isCanStarCardBet(betDo)) {
                // 1.1 是，则星卡投注流程 随机选取波动率
                fillRandomVolatility(betDo);
                return betForStartCard(betDo);
            } else if (isCanFreeSpinBet(betDo)) {
                // 1.1 是，则预定投注流程 随机选取波动率
                fillRandomVolatility(betDo);
                return betForFreeSpin(betDo);
            } else {
                // 1.2 否，则走其他投注流程
                // 1.2.1、判断能否走干预事件投注
                if (isCanOperationEventBet(betDo)) {
                    // 1.2.11 命中干预事件：走干预事件投注  不考虑波动率
                    return betForOperationEvent(betDo);
                } else {
                    // 1.2.12 未命中干预事件：走正常投注。
                    return betForNonFreeWheel(betDo);
                }
            }
        } else {
            return betForFreeWheelQuery(betDo);
        }
    }

    private void fillRandomVolatility(BetDo betDo) {
        AssertUtil.assertNotNull(betDo, ErrorCode.ERR_INTER);
        if (Objects.isNull(betDo.getVolatility())) {
            betDo.setVolatility(VolatilityTypeEnum.random());
        }
    }

    @Override
    protected GamePlayDo buildGamePlayDo(BetDo betDo, long betRecordId, Object extParam) {
        GamePlayDo gamePlayDo = build(betDo, betRecordId);
        if (Objects.nonNull(extParam) && extParam instanceof GamePlayDo.ContinuePlayContext) {
            GamePlayDo.ContinuePlayContext context = (GamePlayDo.ContinuePlayContext) extParam;
            gamePlayDo.setContinuePlayContext(context);
        }
        return gamePlayDo;
    }

    @Override
    protected long getTotalRewardAmount(List<MangkukulamGameRecordDo> list, Object extParam) {
        long currentAmount =  list.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        if (Objects.nonNull(extParam) && extParam instanceof GamePlayDo.ContinuePlayContext) {
            GamePlayDo.ContinuePlayContext context = (GamePlayDo.ContinuePlayContext) extParam;
            currentAmount += context.getTotalPayoutAmount();
        }
        return currentAmount;
    }

    /**
     * 非免费轮投注(正常投注、购买投注)
     */
    private BetResultDo<MangkukulamBetRecordDetailDo> betForNonFreeWheel(BetDo betDo) {
        // 1、生成游戏记录
        long betRecordId = generateBetRecordId();
        if (BetSubTypeEnum.isFirstStep(betDo.getBetSubType().getCode())) {
            // 1、play
            List<MangkukulamGameRecordDo> list = super.play(betDo, betRecordId, mangkukulamConfig.getPayoutAmountLimitConfig(),null);
            AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
            // 2、构建投注记录
            BetRecordDo<MangkukulamGameRecordDo> betRecord = mangkukulamBetHelper.buildBetRecord(betRecordId, betDo, list);
            // 3、投注并派彩
            SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betDo, betRecord, betHelper().hasRewardFreeWheel(list));
            AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
            return mangkukulamBetHelper.handleBetResult(betRecord, resultDo);
        } else if (BetSubTypeEnum.isSecondStep(betDo.getBetSubType().getCode())) {
            // 1、查询投注记录
            AssertUtil.assertNotNull(betDo.getParentBetRecordId(), ErrorCode.ERR_PARAM);
            BetRecordDo<MangkukulamGameRecordDo> preBetRecord = mangkukulamBetRecordManager.getBetRecordByIdFromPrimary(betDo.getParentBetRecordId(), GameTypeEnum.MANGKUKULAM);
            AssertUtil.assertNotNull(preBetRecord, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(validateBetRecordTimeRange(preBetRecord), ErrorCode.ERR_PARAM);
            // 暂时屏蔽bet状态校验，防止因为pulsar消费不及时导致的无法继续玩
//            AssertUtil.assertTrue(Objects.equals(preBetRecord.getStatus(), BetStatusEnum.SUCCESS.getCode()), ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(Objects.equals(preBetRecord.getFreeWheelHandleStatus(), FreeWheelHandleStatusEnum.PENDING.getCode()), ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(StringUtils.equals(preBetRecord.getPlayerId(), UserInfoContext.getUserInfo().getPlayerId()), ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(Objects.equals(betDo.getBetAmount(), preBetRecord.getBetAmount()),ErrorCode.ERR_INTER, "betAmount is not equals to parentBetRecord");
            GamePlayDo.ContinuePlayContext continuePlayContext = buildContinuePlayContext(preBetRecord, betDo);
            // 派彩金额限制
            List<MangkukulamGameRecordDo> list = super.play(betDo, betRecordId, mangkukulamConfig.getPayoutAmountLimitConfig(), continuePlayContext);;// freeWheelHandleStatus 无需处理 待处理 已处理
            AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
            // 2、构建投注记录
            BetRecordDo<MangkukulamGameRecordDo> betRecord = mangkukulamBetHelper.buildBetRecord(betRecordId, betDo, list);
            betRecord.setFreeWheelHandleStatus(FreeWheelHandleStatusEnum.NOT_REQUIRED.getCode());
            // 3、投注并派彩
            SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betDo, betRecord, true);
            AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
            // 4、异步更新投注原始记录 todo，id和parentId加唯一索引
            updatePreRecord(preBetRecord);
            // 5、构建返回结果
            Long lastGameRecordId = continuePlayContext.getParentGameRecordId();
            return mangkukulamBetHelper.handleBetResult(betRecord, list, resultDo.getCredit(), lastGameRecordId);
        } else {
            throw new BusinessException(ErrorCode.ERR_PARAM);
        }
    }

    private void updatePreRecord(BetRecordDo<MangkukulamGameRecordDo> preBetRecord) {
        BetRecordDo updateDo =  BetRecordDo.<MangkukulamGameRecordDo>builder()
                .id(preBetRecord.getId())
                .freeWheelHandleStatus(FreeWheelHandleStatusEnum.PROCESSED.getCode())
                .build();
        mangkukulamBetRecordManager.asyncUpdateBetRecord(updateDo, GameTypeEnum.MANGKUKULAM);
    }

    private boolean validateBetRecordTimeRange(BetRecordDo<MangkukulamGameRecordDo> betRecord) {
        Operator operator = OperatorContext.getOperator();
        // system操作不考虑时间， 用户自己操作要求在4分钟内
        if (Objects.equals(operator.getAdminId(), Constants.SYSTEM_OPERATOR.getAdminId())) {
            return true;
        } else {
            long startTime = System.currentTimeMillis() - (4 * 60 * 1000);
            return betRecord.getCreateTime() > startTime;
        }

    }

    private GamePlayDo.ContinuePlayContext buildContinuePlayContext(BetRecordDo<MangkukulamGameRecordDo> preBetRecord, BetDo betDo) {
        // 1、使用正常投注中的投注金额
        betDo.setBetAmount(preBetRecord.getBetAmount());
        // 2、组装继续玩儿上下文
        // 2.1、查询中免费轮的投注游戏记录
        Sort sort = Sort.by(Sort.Direction.DESC, "index");
        GameRecordQuery gameRecordQuery = GameRecordQuery.builder().betRecordId(betDo.getParentBetRecordId()).sort(sort).build();
        MangkukulamGameRecordDo lastGameRecord = gameRecordService.findOne(gameRecordQuery, GameTypeEnum.MANGKUKULAM, MangkukulamGameRecordDo.class);
        AssertUtil.assertNotNull(lastGameRecord, ErrorCode.ERR_INTER);
        // 3、构建返回结果
        int rewardFreeWheelTimes = lastGameRecord.getRewardResult().getRewardFreeWheelTimes();
        return GamePlayDo.ContinuePlayContext.builder()
                .parentBetRecordId(betDo.getParentBetRecordId())
                .parentGameRecordId(lastGameRecord.getId())
                .totalPayoutAmount(preBetRecord.getPayOutAmount())
                .freeWheelTimes(rewardFreeWheelTimes)
                .build();
    }

    /**
     * 免费轮投注
     */
    private BetResultDo<MangkukulamBetRecordDetailDo> betForFreeWheelQuery(BetDo betDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        GameRecordQuery gameRecordQuery = GameRecordQuery.builder().betRecordId(betDo.getBetRecordId()).playerId(userInfo.getPlayerId()).batchId(betDo.getGameRecordId()).build();
        List<MangkukulamGameRecordDo> gameRecordList = betRecordManager().listGameRecord(gameRecordQuery, gameContext().gameInfo().getGameType());
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_PARAM);
        // 按照index升序排列
        gameRecordList = Optional.ofNullable(gameRecordList)
                .orElse(Collections.emptyList())
                .stream().filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(MangkukulamGameRecordDo::getIndex))
                .collect(Collectors.toList());
        //// 合计中奖总金额
        long overallRewardAmount = MangkukulamCalcUtil.calcTotalRewardAmount(gameRecordList);
        if (CollectionUtils.isNotEmpty(gameRecordList)) {
            MangkukulamGameRecordDo lastGameRecord = gameRecordList.stream()
                    .filter(item -> BooleanUtils.isTrue(item.isLastGameFlag()))
                    .findFirst()
                    .orElse(null);
            if (Objects.nonNull(lastGameRecord)) {
                // 后处理-免费轮
                freeWheelAfterHandle(lastGameRecord.getBetRecordId());
            }
        }
        return BetResultDo.<MangkukulamBetRecordDetailDo>builder()
                .betRecordId(betDo.getBetRecordId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .overallRewardAmount(overallRewardAmount)
                .totalPayoutAmount(0L)
                .resultDetail(MangkukulamBetRecordDetailDo
                        .builder()
                        .allWheelRewardSubType(RewardHelper.calculateRewardSubType(betDo.getBetAmount(), overallRewardAmount))
                        .gameRecordList(gameRecordList).build())
                .build();
    }


    public void freeWheelAfterHandle(Long betRecordId) {
        // do nothing
    }

}
