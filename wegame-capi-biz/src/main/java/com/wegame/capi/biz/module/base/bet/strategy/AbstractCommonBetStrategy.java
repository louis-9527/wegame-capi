package com.wegame.capi.biz.module.base.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.operation.OperationEventDetailBuilder;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetContextDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.GameOperationEventDo;
import com.wegame.capi.biz.domain.biz.promotion.RedeemRewardResultDo;
import com.wegame.capi.biz.domain.biz.promotion.RewardDetailDo;
import com.wegame.capi.biz.domain.biz.promotion.StarCardUseRecordDo;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.biz.service.promotion.BagService;
import com.wegame.capi.biz.service.promotion.StarCardUseRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.enums.*;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.infrastructure.config.properties.GameApiProperty;
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
import static com.wegame.capi.client.enums.BetTypeEnum.NORMAL_BET;
import static com.wegame.capi.common.enums.BetPromotionTypeEnum.*;

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
    StarCardUseRecordService startCardUseRecordService;
    @Autowired
    BagService bagService;
    @Autowired
    private GameApiProperty gameApiProperty;
    @Value("${dynamic.operation.switch}")
    public boolean operationSwitch;

    public abstract AbstractOperationEventManager operationEventManager();

    public abstract BetHelper<RD, R> betHelper();

    public boolean isCanOperationEventBet(BetContextDo betContextDo) {
        if (!operationSwitch) {
            return false;
        }
        try {
            boolean canOperationEventBet = false;
            UserInfo userInfo = UserInfoContext.getUserInfo();
            AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
            AssertUtil.assertNotNull(userInfo.getChannelId(), ErrorCode.ERR_NOT_LOGIN);
            // 判断渠道
            if (!gameApiProperty.getChannelIdAll().contains(userInfo.getChannelId())) {
                return false;
            }
            // 仅正常投注时可干预
            if (Objects.equals(betContextDo.getBetType(), NORMAL_BET)) {
                GameOperationEventDo gameOperationEvent = operationEventManager().getEventBetResult(betContextDo.getPayAmount(), userInfo.getChannelId());
                canOperationEventBet = Objects.nonNull(gameOperationEvent) && gameOperationEvent.isEventResult();
                if (canOperationEventBet) {
                    // 设置betDo
                    betContextDo.setGameOperationEvent(gameOperationEvent);
                    betContextDo.setBetPromotionType(BetPromotionTypeEnum.OPERATION_EVENT);
                }
            }
            return canOperationEventBet;
        } catch (Exception e) {
            log.error("isCanOperationEventBet error, errMsg: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 干预事件投注-通用投注流程
     */
    public BetResultDo<RD> betForOperationEvent(BetContextDo betContextDo) {
        // 数据校验
        AssertUtil.assertNotNull(betContextDo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(betContextDo.getGameOperationEvent(), ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(betContextDo.getGameOperationEvent().isEventResult(), ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(Objects.equals(betContextDo.getBetPromotionType(), OPERATION_EVENT), ErrorCode.ERR_INTER);
        GameOperationEventDo gameOperationEvent = betContextDo.getGameOperationEvent();
        log.info("betForOperationEvent betContextDo:{}", betContextDo.getGameOperationEvent());
        // 生产投注记录
        BetRecordDo<R> betRecordDo = generateBetRecord(generateBetRecordId(), betContextDo);
        // 保存干预事件详情并修改干预事件干预状态
        operationEventManager().updateOperationUseStatus(gameOperationEvent.getOperationEvent().getId(), OperationEventStatusEnum.ING);
        Long eventDetailId = operationEventManager().addEventDetail(OperationEventDetailBuilder.build(generateId(), betRecordDo, gameOperationEvent));
        AssertUtil.assertNotNull(eventDetailId, ErrorCode.ERR_INTER);
        // 投注并派奖
        boolean syncOpt = betHelper().hasRewardFreeWheel(betRecordDo.getGameRecordList());
        SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betContextDo, betRecordDo, syncOpt);
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        // 异步更新干预事件&干预事件详情状态
        long eventAmount = betRecordDo.getPayOutAmount() - betRecordDo.getPayAmount();
        if (FINAL_BET_STATUS.contains(resultDo.getBetStatus())) {
            operationEventManager().asyncUpdateEventDetail(gameOperationEvent.getOperationEvent(),
                    UserInfoContext.getUserInfo().getPlayerName(),
                    resultDo.getBetStatus(),
                    eventDetailId,
                    resultDo.getBetId(),
                    eventAmount);
        }
        return betHelper().handleBetResult(betRecordDo, resultDo);
    }

    public boolean isCanStarCardBet(BetContextDo betContextDo) {
        try {
            if (!betContextDo.isUseStarCard()) {
                return false;
            }
            // 查询星卡详情
            RewardDetailDo rewordDetail = bagService.getRewardDetail(betContextDo.getRewardConfigId());
            AssertUtil.assertNotNull(rewordDetail, ErrorCode.STAR_CARD_USE_ERROR);
            AssertUtil.assertNotNull(rewordDetail.getDetails(), ErrorCode.STAR_CARD_USE_ERROR);
            AssertUtil.assertTrue(Objects.equals(rewordDetail.getDetails().getBetValue(), betContextDo.getBetAmount()), ErrorCode.ERR_PARAM);
            // 设置betDo
            betContextDo.setBetPromotionType(STAR_CARD);
            betContextDo.setRewardDetail(rewordDetail);
            return true;
        } catch (Exception e) {
            log.error("isCanOperationEventBet error, errMsg: {}", e.getMessage(), e);
            throw e;
        }
    }

    public boolean isCanFreeSpinBet(BetContextDo betContextDo) {
        try {
            if (!betContextDo.isUseFreeSpin()) {
                return false;
            }
            // 设置betDo
            betContextDo.setBetPromotionType(FREE_SPIN);
            return true;
        } catch (Exception e) {
            log.error("isCanFreeSpinBet error, errMsg: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 星卡投注流程-通用流程
     * <p>
     * todo 优化
     */
    public BetResultDo<RD> betForStartCard(BetContextDo betContextDo) {
        AssertUtil.assertNotNull(betContextDo, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(betContextDo.isUseStarCard(), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(betContextDo.getRewardDetail(), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(betContextDo.getRewardDetail().getDetails(), ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(Objects.equals(betContextDo.getBetPromotionType(), STAR_CARD), ErrorCode.ERR_INTER);
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        // 生成投注记录Id：唯一
        long betRecordId = generateBetRecordId();
        Long startCardUseRecordId = startCardUseRecordService.add(StarCardUseRecordDo.builder()
                .id(generateId())
                .gameId(gameContext().gameInfo().getGameType().getId())
                .channelId(userInfo.getChannelId())
                .betAmount(betContextDo.getBetAmount())
                .playerId(userInfo.getPlayerId())
                .rewardId(betContextDo.getRewardId())
                .betRecordId(betRecordId)
                .rewardConfigId(betContextDo.getRewardConfigId())
                .playName(userInfo.getPlayerName())
                .useStatus(StarCardUseStatusEnum.ING)
                .reportStatus(StarCardReportStatusEnum.ING)
                .build(), this.gameContext().gameInfo().getGameType());
        // 核销星卡
        long startTime = System.currentTimeMillis();
        Pair<RedeemRewardResultDo, BetRecordDo<R>> redeemRewardResultDoBetRecord = this.rewardResult(betRecordId, betContextDo);
        log.info("rewardResult cost time: {} ms", System.currentTimeMillis() - startTime);
        RedeemRewardResultDo rewardResult = redeemRewardResultDoBetRecord.getKey();
        BetRecordDo<R> rBetRecordDo = redeemRewardResultDoBetRecord.getRight();
        if (!rewardResult.isSuccess()) {
            // 更新星卡使用状态-失败
            startCardUseRecordService.asyncUpdate(StarCardUseRecordDo.builder()
                    .id(startCardUseRecordId)
                    .useStatus(StarCardUseStatusEnum.FAIL)
                    .reportStatus(StarCardReportStatusEnum.NONE)
                    .failCode(rewardResult.getStatus())
                    .failMsg(rewardResult.getMessage())
                    .build(), this.gameContext().gameInfo().getGameType());
            if (rewardResult.getErrorCode() != null) {
                throw new BusinessException(rewardResult.getErrorCode());
            }
            throw new BusinessException(ErrorCode.STAR_CARD_USE_ERROR);
        }
        // 更新星卡使用状态-成功
        startCardUseRecordService.asyncUpdate(StarCardUseRecordDo.builder()
                .id(startCardUseRecordId)
                .payOutAmount(rBetRecordDo.getPayOutAmount())
                .useStatus(StarCardUseStatusEnum.SUCCESS)
                .build(), this.gameContext().gameInfo().getGameType());
        // 调用投注
        boolean syncOpt = betHelper().hasRewardFreeWheel(rBetRecordDo.getGameRecordList());
        SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betContextDo, rBetRecordDo, syncOpt);
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.SUCCESS)) {
            // 更新星卡上报状态->无需上报
            startCardUseRecordService.asyncUpdate(StarCardUseRecordDo.builder()
                    .id(startCardUseRecordId)
                    .reportStatus(StarCardReportStatusEnum.NONE)
                    .build(), this.gameContext().gameInfo().getGameType());
        } else {
            // todo 调用单钱包失败需要上报,待成就系统开发完成接入
        }
        return betHelper().handleBetResult(rBetRecordDo, resultDo);
    }

    public BetResultDo<RD> betForFreeSpin(BetContextDo betContextDo) {
        AssertUtil.assertNotNull(betContextDo, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(betContextDo.isUseFreeSpin(), ErrorCode.ERR_INTER);
        AssertUtil.assertStrNotEmpty(betContextDo.getCampaignId(), ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(Objects.equals(betContextDo.getBetPromotionType(), FREE_SPIN), ErrorCode.ERR_INTER);
        BetRecordDo<R> betRecordDo = generateBetRecord(generateBetRecordId(), betContextDo);
        boolean syncOpt = betHelper().hasRewardFreeWheel(betRecordDo.getGameRecordList());
        SingleWalletBetResultDo resultDo = saveBetRecordAndBetWithPayout(betContextDo, betRecordDo, syncOpt);
        return betHelper().handleBetResult(betRecordDo, resultDo);
    }

    public BetRecordDo<R> generateBetRecord(long betRecordId, BetContextDo betContextDo) {
        buildGamePlayType(betContextDo);
        List<R> list = gameContext().play(build(betContextDo, betRecordId));
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        return betHelper().buildBetRecord(betRecordId, betContextDo, list);
    }

    /**
     * 核销星卡
     * 如果第一次返回5017(超过最大限额度),需要重试
     */
    private Pair<RedeemRewardResultDo, BetRecordDo<R>> rewardResult(long betRecordId, BetContextDo betContextDo) {
        BetRecordDo<R> rBetRecordDo = generateBetRecord(betRecordId, betContextDo);
        RedeemRewardResultDo rewardResult = bagService.use(betContextDo.getRewardId(), rBetRecordDo.getPayOutAmount(), rBetRecordDo.getId());
        if (rewardResult.isSuccess()) {
            return Pair.of(rewardResult, rBetRecordDo);
        } else if (Objects.equals(Integer.valueOf(AchievementErrorCode.INVALID_REWARD_PAYOUT.getThirdCode()), rewardResult.getStatus())) {
            // 派奖金额超过限制
            long maxPay = AmountUtil.toLong(new BigDecimal(rewardResult.getMessage()));
            log.warn("rewardResult info, status: {}, maxPay: {},PayOutAmount:{}", rewardResult.getStatus(), maxPay, rBetRecordDo.getPayOutAmount());
            for (int i = 1; i <= 10; i++) {
                rBetRecordDo = generateBetRecord(betRecordId, betContextDo);
                if (rBetRecordDo.getPayOutAmount() < maxPay) {
                    break;
                }
                log.warn(" try rewardResult info,  maxPay: {},PayOutAmount:{}", maxPay, rBetRecordDo.getPayOutAmount());
                // 如果重试10次都未获取到结果,返回异常
                if (i == 10) {
                    log.error("try then 10 rewardResult info,RewardId:{}", betContextDo.getRewardId());
                    rewardResult.setErrorCode(ErrorCode.ERR_INTER);
                    return Pair.of(rewardResult, rBetRecordDo);
                }
            }
            rewardResult = bagService.use(betContextDo.getRewardId(), rBetRecordDo.getPayOutAmount(), rBetRecordDo.getId());
            return Pair.of(rewardResult, rBetRecordDo);
        } else {
            AchievementErrorCode achievementErrorCode = AchievementErrorCode.valueOfThirdCode(rewardResult.getStatus());
            rewardResult.setErrorCode(achievementErrorCode == null ? ErrorCode.ERR_MIDDLE_PLATFORM : achievementErrorCode.getErrorCode());
            return Pair.of(rewardResult, rBetRecordDo);
        }
    }
}
