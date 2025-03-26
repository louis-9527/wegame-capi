package com.wegame.capi.biz.module.base.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.lock.DistributeLock;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.operation.OperationEventDetailBuilder;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.GameOperationEventDo;
import com.wegame.capi.biz.domain.biz.promotion.RedeemRewardResultDo;
import com.wegame.capi.biz.domain.biz.promotion.RewardDetailDo;
import com.wegame.capi.biz.domain.biz.promotion.StarCardUseRecordDo;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.module.base.bet.function.BetRecordFunction;
import com.wegame.capi.biz.module.base.bet.function.BetResultHandleFunction;
import com.wegame.capi.biz.service.promotion.BagService;
import com.wegame.capi.biz.service.promotion.StarCardUseRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.RedeemRewardResStatusEnum;
import com.wegame.capi.common.enums.StarCardReportStatusEnum;
import com.wegame.capi.common.enums.StarCardUseStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.wegame.capi.biz.builder.GamePlayBuilder.build;

/**
 * 抽象通用投注策略<br>
 * <p>
 * 抽象通用投注流程
 */
@Slf4j
@Component
public abstract class AbstractCommonBetStrategy<R extends ToVoConverter, RD extends ToVoConverter>
        extends AbstractBetStrategy<R, RD> {

    private static final List<BetStatusEnum> FINAL_BET_STATUS = Arrays.asList(BetStatusEnum.SUCCESS, BetStatusEnum.FAIL);

    @Autowired
    DistributeLock distributeLock;
    @Autowired
    StarCardUseRecordService startCardUseRecordService;
    @Autowired
    BagService bagService;
    @Value("${api.game.cp.channelId}")
    public String cpChannelId;

    public abstract AbstractOperationEventManager operationEventManager();

    /**
     * 干预事件投注-通用投注流程
     */
    public BetResultDo<RD> betForOperationEvent(BetDo betDo,
                                                boolean syncOpt,
                                                BetRecordFunction<R> betRecordFunc,
                                                BetResultHandleFunction<RD, R> betResultHandleFunc) {
        // 前置处理 来自BetDo todo 前置枷锁 @bruce
        // 数据校验
        AssertUtil.assertNotNull(betDo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(betDo.getGameOperationEvent(), ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(betDo.getGameOperationEvent().isEventResult(), ErrorCode.ERR_INTER);
        GameOperationEventDo gameOperationEvent = betDo.getGameOperationEvent();
        // 生产投注记录
        BetRecordDo<R> betRecordDo = generateBetRecord(generateBetRecordId(), betDo, betRecordFunc);
        // 保存干预事件详情并修改干预事件干预状态
        Long eventDetailId = operationEventManager().addEventDetail(OperationEventDetailBuilder.build(generateId(), betRecordDo, gameOperationEvent));
        AssertUtil.assertNotNull(eventDetailId, ErrorCode.ERR_INTER);
        // 投注并派奖 todo // this.hasRewardFreeWheel(betRecordDo.getGameRecordList())
        SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betRecordDo, syncOpt);
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        // 异步更新干预事件&干预事件详情状态
        long eventAmount = betRecordDo.getPayOutAmount() - betRecordDo.getPayAmount();
        if (FINAL_BET_STATUS.contains(resultDo.getBetStatus())) {
            operationEventManager().asyncUpdateEventDetail(gameOperationEvent.getOperationEvent(),
                    resultDo.getBetStatus(),
                    eventDetailId,
                    resultDo.getBetId(),
                    eventAmount);
        }
        return betResultHandleFunc.handle(betRecordDo, resultDo);
    }

    /**
     * 星卡投注流程-通用流程
     *
     * todo 优化
     */
    public BetResultDo<RD> betForStartCard(BetDo betDo,
                                           boolean syncOpt,
                                           BetRecordFunction<R> betRecordFunc,
                                           BetResultHandleFunction<RD, R> betResultHandleFunc) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        // 查询星卡详情
        RewardDetailDo rewordDetail = bagService.getRewardDetail(betDo.getRewardConfigId());
        AssertUtil.assertNotNull(rewordDetail, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(rewordDetail.getDetails(), ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(Objects.equals(rewordDetail.getDetails().getBetValue(), betDo.getBetAmount()), ErrorCode.ERR_PARAM);
        // 准备核销星卡
        StarCardUseRecordDo recordDo = StarCardUseRecordDo.builder()
                .id(super.generateId())
                .gameId(this.gameContext().gameInfo().getGameType().getId())
                .betAmount(betDo.getBetAmount())
                .playerId(userInfo.getPlayerId())
                .rewardId(betDo.getRewardId())
                .rewardConfigId(betDo.getRewardConfigId())
                .playName(userInfo.getPlayerName()).build();
        Long useRecordId = startCardUseRecordService.add(recordDo, this.gameContext().gameInfo().getGameType());
        // 核销星卡
        // 生成投注记录Id：唯一
        long betRecordId = generateBetRecordId();
        Pair<RedeemRewardResultDo, BetRecordDo<R>> redeemRewardResultDoBetRecord = this.rewardResult(betRecordId, betDo, betRecordFunc);
        RedeemRewardResultDo rewardResult = redeemRewardResultDoBetRecord.getKey();
        BetRecordDo<R> rBetRecordDo = redeemRewardResultDoBetRecord.getRight();
        if (!rewardResult.getSuccess()) {
            // 更新星卡使用状态-失败
            StarCardUseRecordDo updateRecord = StarCardUseRecordDo.builder()
                    .id(useRecordId)
                    .useStatus(StarCardUseStatusEnum.FAIL)
                    .reportStatus(StarCardReportStatusEnum.NONE)
                    .failMsg(rewardResult.getMessage())
                    .playName(userInfo.getPlayerName()).build();
            startCardUseRecordService.update(updateRecord, this.gameContext().gameInfo().getGameType());
            if (rewardResult.getErrorCode() != null) {
                throw new BusinessException(rewardResult.getErrorCode());
            }
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
        // 更新星卡使用状态-成功
        StarCardUseRecordDo updateRecord = StarCardUseRecordDo.builder()
                .id(useRecordId)
                .payOutAmount(rBetRecordDo.getPayOutAmount())
                .betRecordId(rBetRecordDo.getId())
                .useStatus(StarCardUseStatusEnum.SUCCESS)
                .playName(userInfo.getPlayerName()).build();
        startCardUseRecordService.update(updateRecord, this.gameContext().gameInfo().getGameType());
        // 调用投注 // todo this.hasRewardFreeWheel(rBetRecordDo.getGameRecordList())
        SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(rBetRecordDo, syncOpt);
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.SUCCESS)) {
            // 更新星卡上报状态->无需上报
            StarCardUseRecordDo updateSuccessRecord = StarCardUseRecordDo.builder()
                    .id(useRecordId)
                    .reportStatus(StarCardReportStatusEnum.NONE)
                    .playName(userInfo.getPlayerName()).build();
            startCardUseRecordService.update(updateSuccessRecord, this.gameContext().gameInfo().getGameType());
        } else {
            // todo 调用单钱包失败需要上报,待成就系统开发完成接入
        }
        return betResultHandleFunc.handle(rBetRecordDo, resultDo);
    }

    public BetRecordDo<R> generateBetRecord(long betRecordId,
                                            BetDo betDo,
                                            BetRecordFunction<R> betRecordFunc) {
        GamePlayTypeEnum playTypeEnum = GamePlayTypeEnum.NORMAL_PLAY;
        String ieId = null;
        GameOperationEventDo gameEventDo = betDo.getGameOperationEvent();
        if (gameEventDo != null && gameEventDo.isEventResult()) {
            playTypeEnum = GamePlayTypeEnum.EVENT_PLAY;
            ieId = gameEventDo.getOperationEvent().getEventId();
        }
        List<R> list = gameContext().play(build(playTypeEnum, betDo, betRecordId, gameEventDo));
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        return betRecordFunc.build(betRecordId, betDo, list, ieId);
    }

    /**
     * 核销星卡
     * 如果第一次返回5017(超过最大限额度),需要重试
     */
    private Pair<RedeemRewardResultDo, BetRecordDo<R>> rewardResult(long betRecordId, BetDo betDo, BetRecordFunction<R> betRecordFunc) {
        BetRecordDo<R> rBetRecordDo = generateBetRecord(betRecordId, betDo, betRecordFunc);
        RedeemRewardResultDo rewardResult = bagService.use(betDo.getRewardId(), betDo.getPayAmount(), rBetRecordDo.getId());
        if (rewardResult.getSuccess()) {
            return Pair.of(rewardResult, rBetRecordDo);
        } else if (Objects.equals(RedeemRewardResStatusEnum.INSUFFICIENT_CREDIT.getCode(), rewardResult.getStatus())) {
            // 余额-不足
            rewardResult.setErrorCode(ErrorCode.INSUFFICIENT_CREDIT);
            return Pair.of(rewardResult, rBetRecordDo);
        } else if (Objects.equals(RedeemRewardResStatusEnum.INVALID_REWARD_PAYOUT.getCode(), rewardResult.getStatus())) {
            // 派奖金额超过限制
            long maxPay = AmountUtil.toLong(new BigDecimal(rewardResult.getMessage()));
            for (int i = 1; i <= 10; i++) {
                rBetRecordDo = generateBetRecord(betRecordId, betDo, betRecordFunc);
                if (rBetRecordDo.getPayAmount() < maxPay) {
                    break;
                }
                // 如果重试10次都未获取到结果,返回异常
                if (i == 10) {
                    rewardResult.setErrorCode(ErrorCode.ERR_MIDDLE_PLATFORM);
                    return Pair.of(rewardResult, rBetRecordDo);
                }
            }
            rewardResult = bagService.use(betDo.getRewardId(), rBetRecordDo.getPayOutAmount(), rBetRecordDo.getId());
            return Pair.of(rewardResult, rBetRecordDo);
        }
        return Pair.of(rewardResult, rBetRecordDo);
    }
}
