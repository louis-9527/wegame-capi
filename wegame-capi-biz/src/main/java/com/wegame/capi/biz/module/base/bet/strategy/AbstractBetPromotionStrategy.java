package com.wegame.capi.biz.module.base.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.LockException;
import com.wegame.base.lock.DistributeLock;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.operation.OperationEventDetailBuilder;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.GameOperationEventDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDo;
import com.wegame.capi.biz.domain.biz.promotion.RedeemRewardResultDo;
import com.wegame.capi.biz.domain.biz.promotion.RewardDetailDo;
import com.wegame.capi.biz.domain.biz.promotion.StarCardUseRecordDo;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.service.promotion.BagService;
import com.wegame.capi.biz.service.promotion.StarCardUseRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.RedeemRewardResStatusEnum;
import com.wegame.capi.common.enums.StarCardReportStatusEnum;
import com.wegame.capi.common.enums.StarCardUseStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.common.utils.LockNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.wegame.capi.biz.builder.GamePlayBuilder.build;

@Slf4j
public abstract class AbstractBetPromotionStrategy<R extends ToVoConverter, DR extends ToVoConverter>
        extends AbstractBetStrategy<R, DR> {

    @Autowired
    DistributeLock distributeLock;
    @Autowired
    private StarCardUseRecordService startCardUseRecordService;
    @Autowired
    private BagService bagService;
    @Value("${api.game.cp.channelId}")
    private String cpChannelId;

    public abstract AbstractOperationEventManager operationEventManager();

    @Override
    public BetResultDo<DR> bet(BetDo betDo) {
        if (!BetSubTypeEnum.isFreeWheel(betDo.getBetSubType().getCode())) {
            // 非免费轮投注
            return betForNonFreeWheel(betDo);
        } else {
            // 免费轮投注, 走查询
            return betForFreeWheel(betDo);
        }
    }


    public BetResultDo<DR> betForNonFreeWheel(BetDo betDo) {
        // 判断是否使用星卡
        boolean useStartCard = betDo.isUseStarCart();
        if (useStartCard) {
            // 使用星卡投注
            return this.betForStartCard(betDo);
        }
        UserInfo userInfo = UserInfoContext.getUserInfo();
        if (Objects.equals(cpChannelId, userInfo.getChannelId()) && betDo.getBetType() == BetTypeEnum.NORMAL_BET) {
            // 动态运营
            return this.betForDynamic(betDo);
        }
        // 正常投注
        BetRecordDo<R> betRecordDo = this.loadBetRecordDo(betDo, null);
        SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betRecordDo, this.hasRewardFreeWheel(betRecordDo.getGameRecordList()));
        return this.buildResult(betRecordDo, resultDo);
    }


    private BetResultDo<DR> betForStartCard(BetDo betDo) {
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
        Pair<RedeemRewardResultDo, BetRecordDo<R>> redeemRewardResultDoBetRecord = this.rewardResult(betDo);
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
        //调用投注
        SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(rBetRecordDo, this.hasRewardFreeWheel(rBetRecordDo.getGameRecordList()));
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
        return this.buildResult(rBetRecordDo, resultDo);
    }


    private BetResultDo<DR> betForDynamic(BetDo betDo) {
        // 判断是否命中干预
        Pair<GameOperationEventDo, BetRecordDo<R>> eventBetRecordPair = this.getEventBetRecordPair(betDo);
        if (Objects.isNull(eventBetRecordPair)) {
            BetRecordDo<R> betRecordDo = this.loadBetRecordDo(betDo, null);
            SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betRecordDo, this.hasRewardFreeWheel(betRecordDo.getGameRecordList()));
            return this.buildResult(betRecordDo, resultDo);
        }
        BetRecordDo<R> betRecordDo = eventBetRecordPair.getValue();
        GameOperationEventDo eventBetResult = eventBetRecordPair.getKey();
        // 投注并派奖
        SingleWalletBetResultDo resultDo = super.saveBetRecordAndBetWithPayout(betRecordDo, this.hasRewardFreeWheel(betRecordDo.getGameRecordList()));
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        // 命中干预事件,从结果中获取数据
        BetStatusEnum betStatusEnum = BetStatusEnum.FAIL;
        if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.SUCCESS)) {
            betStatusEnum = BetStatusEnum.SUCCESS;
        }
        // 命中的事件信息
        OperationEventDo eventDo = eventBetResult.getOperationEvent();
        // 异步上报、更新状态
        long eventAmount = betRecordDo.getPayOutAmount() - betRecordDo.getPayAmount();
        operationEventManager().asyncUpdateEventDetail(eventDo, betStatusEnum, eventBetResult.getOperatorRecordId(), resultDo.getBetId(), eventAmount);
        return this.buildResult(betRecordDo, resultDo);
    }


    private Pair<GameOperationEventDo, BetRecordDo<R>> getEventBetRecordPair(BetDo betDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        String operationEventBetLockName = LockNameUtil.getOperationEventBetLockName(this.gameContext().gameInfo().getGameType(), userInfo.getPlayerId());
        return distributeLock.tryLockThenCall(operationEventBetLockName,    500L, 1000L, () -> {
            try {
                GameOperationEventDo eventBetResult = operationEventManager().getEventBetResult(betDo.getPayAmount());
                if (eventBetResult.getEventResult()) {
                    BetRecordDo<R> betRecordDo = this.loadBetRecordDo(betDo, eventBetResult);
                    betRecordDo.setIeId(eventBetResult.getOperationEvent().getEventId());
                    // 构建并保存干预记录
                    Long detailId = this.saveEventDetail(generateBetRecordId(), betRecordDo, eventBetResult);
                    eventBetResult.setOperatorRecordId(detailId);
                    return Pair.of(eventBetResult, betRecordDo);
                }
                return null;

            } catch (LockException e) {
                log.info("bet for dynamic , get lock fail, gameType: {}, playerId: {}", this.gameContext().gameInfo().getGameType().getId(), userInfo.getPlayerId());
                throw new BusinessException(ErrorCode.ERR_NET);
            }
        });
    }

    private BetRecordDo<R> loadBetRecordDo(BetDo betDo, GameOperationEventDo gameEventDo) {
        //获取投注记录
        long betRecordId = generateBetRecordId();
        GamePlayTypeEnum playTypeEnum = GamePlayTypeEnum.NORMAL_PLAY;
        if (gameEventDo != null && gameEventDo.getEventResult()) {
            playTypeEnum = GamePlayTypeEnum.EVENT_PLAY;
        }
        List<R> list = gameContext().play(build(playTypeEnum, betDo, betRecordId, gameEventDo));
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        // 构建投注记录
        return this.buildBetRecord(betRecordId, betDo, list);
    }

    /**
     * 核销星卡
     * 如果第一次返回5017(超过最大限额度),需要重试
     */

    private Pair<RedeemRewardResultDo, BetRecordDo<R>> rewardResult(BetDo betDo) {
        BetRecordDo<R> rBetRecordDo = loadBetRecordDo(betDo, null);
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
                rBetRecordDo = loadBetRecordDo(betDo, null);
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


    private Long saveEventDetail(Long eventDetailId, BetRecordDo betRecordDo, GameOperationEventDo eventDo) {
        return operationEventManager().addEventDetail(OperationEventDetailBuilder.build(eventDetailId, betRecordDo, eventDo));
    }

    /**
     * 根据投注记录和单钱包返回结果
     * 构建返回结果
     *
     * @param rBetRecordDo
     * @param resultDo
     * @return
     */
    public abstract BetResultDo<DR> buildResult(BetRecordDo<R> rBetRecordDo, SingleWalletBetResultDo resultDo);

    /**
     * 构建投注记录
     *
     * @param betRecordId
     * @param betDo
     * @param list
     * @return
     */
    public abstract BetRecordDo<R> buildBetRecord(Long betRecordId, BetDo betDo, List<R> list);

    /**
     * 免费轮查询
     *
     * @param betDo
     * @return
     */
    public abstract BetResultDo<DR> betForFreeWheel(BetDo betDo);

    public abstract boolean hasRewardFreeWheel(List<R> gameRecordList);
}



