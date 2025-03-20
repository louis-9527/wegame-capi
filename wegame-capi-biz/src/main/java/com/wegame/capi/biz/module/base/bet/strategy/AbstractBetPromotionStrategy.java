package com.wegame.capi.biz.module.base.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.BetSingleWalletResultDo;
import com.wegame.capi.biz.domain.biz.bet.StarCardUseDo;
import com.wegame.capi.biz.domain.biz.game.GameEventDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDo;
import com.wegame.capi.biz.domain.biz.promotion.RedeemRewardResultDo;
import com.wegame.capi.biz.domain.biz.promotion.RewardDetailDo;
import com.wegame.capi.biz.domain.biz.promotion.StarCardUseRecordDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.manager.AbstractOperationEventManager;
import com.wegame.capi.biz.service.promotion.BagService;
import com.wegame.capi.biz.service.promotion.StarCardUseRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.StarCardReportStatusEnum;
import com.wegame.capi.common.enums.StarCardUseStatusEnum;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Objects;

public abstract class AbstractBetPromotionStrategy<R extends ToVoConverter, BD, BR extends ToVoConverter, WD extends SymbolDo> extends AbstractBetStrategy<R, BD, BR> {

    @Autowired
    private StarCardUseRecordService startCardUseRecordService;
    @Autowired
    private BagService bagService;
    @Value("${api.game.cp.channelId}")
    private String cpChannelId;

    public abstract AbstractOperationEventManager<WD> getDynamicOperationManager();

    @Override
    public BetResultDo<BR> bet(BetDo<BD> betDo) {
        if (!BetSubTypeEnum.isFreeWheel(betDo.getBetSubType().getCode())) {
            // 非免费轮投注
            return betForNonFreeWheel(betDo);
        } else {
            // 免费轮投注, 走查询
            return betForFreeWheel(betDo);
        }
    }


    public BetResultDo<BR> betForNonFreeWheel(BetDo<BD> betDo) {
        // 判断是否使用星卡
        boolean useStartCard = betDo.getStarCardUseDo() != null;
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
        BetSingleWalletResultDo resultDo = saveBetRecordAndBetWithPayout(betRecordDo, this.hasRewardFreeWheel(betRecordDo.getGameRecordList()));
        return this.buildResult(betRecordDo, resultDo);
    }


    private BetResultDo<BR> betForStartCard(BetDo<BD> betDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        Pair<Long, Long> betAndPayAmount = this.getBetAndPayAmount(betDo);

        // 使用星卡
        StarCardUseDo starCardUse = betDo.getStarCardUseDo();
        // 查询星卡详情
        RewardDetailDo rewordDetail = bagService.getByRewardConfigId(starCardUse.getRewardConfigId());
        AssertUtil.assertNotNull(rewordDetail, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(rewordDetail.getDetails(), ErrorCode.ERR_PARAM);

        AssertUtil.assertTrue(Objects.equals(rewordDetail.getDetails().getBetValue(), betAndPayAmount.getKey()), ErrorCode.ERR_PARAM);
        // 准备核销星卡
        StarCardUseRecordDo recordDo = StarCardUseRecordDo.builder()
                .id(super.generateBetRecordId())
                .gameId(this.getGame().getGameInfo().getGameType().getId())
                .betAmount(betAndPayAmount.getKey())
                // .payOutAmount(rBetRecordDo.getPayOutAmount())
                .playerId(userInfo.getPlayerId())
                .playName(userInfo.getPlayerName()).build();
        Long useRecordId = startCardUseRecordService.add(recordDo, this.getGame().getGameInfo().getGameType());

        // 核销星卡
        Pair<RedeemRewardResultDo, BetRecordDo<R>> redeemRewardResultDoBetRecord = this.rewardResult(betDo);
        RedeemRewardResultDo rewardResult = redeemRewardResultDoBetRecord.getKey();
        BetRecordDo<R> rBetRecordDo = redeemRewardResultDoBetRecord.getRight();
        if (!rewardResult.getSuccess()) {
            // 更新星卡使用状态-失败
            StarCardUseRecordDo updateRecord = StarCardUseRecordDo.builder()
                    .id(useRecordId)
                    .useStatus(StarCardUseStatusEnum.FAIL)
                    .failMsg(rewardResult.getMessage())
                    .playName(userInfo.getPlayerName()).build();
            startCardUseRecordService.update(updateRecord, this.getGame().getGameInfo().getGameType());
            throw new BusinessException(ErrorCode.ERR_PARAM);
        }
        // 更新星卡使用状态-成功
        StarCardUseRecordDo updateRecord = StarCardUseRecordDo.builder()
                .id(useRecordId)
                .payOutAmount(rBetRecordDo.getPayOutAmount())
                .useStatus(StarCardUseStatusEnum.SUCCESS)
                .playName(userInfo.getPlayerName()).build();
        startCardUseRecordService.update(updateRecord, this.getGame().getGameInfo().getGameType());
        //调用投注
        BetSingleWalletResultDo resultDo = saveBetRecordAndBetWithPayout(rBetRecordDo, this.hasRewardFreeWheel(rBetRecordDo.getGameRecordList()));
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.SUCCESS)) {
            // 更新星卡上报状态->无需上报
            StarCardUseRecordDo updateRecord2 = StarCardUseRecordDo.builder()
                    .id(useRecordId)
                    .reportStatus(StarCardReportStatusEnum.NONE)
                    .playName(userInfo.getPlayerName()).build();
            startCardUseRecordService.update(updateRecord2, this.getGame().getGameInfo().getGameType());
        } else {
            // todo 调用单钱包失败需要上报
        }
        return this.buildResult(rBetRecordDo, resultDo);
    }


    private BetResultDo<BR> betForDynamic(BetDo<BD> betDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        Pair<Long, Long> betAndPayAmount = this.getBetAndPayAmount(betDo);
        // 判断是否命中干预
        GameEventDo<WD> eventBetResult = getDynamicOperationManager().getEventBetResult(betAndPayAmount.getValue());
        if (!eventBetResult.getEventResult()) {
            BetRecordDo<R> betRecordDo = this.loadBetRecordDo(betDo, null);
            BetSingleWalletResultDo resultDo = saveBetRecordAndBetWithPayout(betRecordDo, this.hasRewardFreeWheel(betRecordDo.getGameRecordList()));
            return this.buildResult(betRecordDo, resultDo);
        } else {
            BetRecordDo<R> betRecordDo = this.loadBetRecordDo(betDo, eventBetResult);
            betRecordDo.setIeId(eventBetResult.getDynamicOperationEvent().getEventId());
            // 构建并保存干预记录
            Long detailId = this.saveEventDetail(userInfo, generateBetRecordId(), betRecordDo, eventBetResult);
            // 投注并派奖
            BetSingleWalletResultDo resultDo = super.saveBetRecordAndBetWithPayout(betRecordDo, this.hasRewardFreeWheel(betRecordDo.getGameRecordList()));
            AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
            // 命中干预事件,从结果中获取数据
            BetStatusEnum betStatusEnum = BetStatusEnum.FAIL;
            if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.SUCCESS)) {
                betStatusEnum = BetStatusEnum.SUCCESS;
            }
            // 命中的事件信息
            OperationEventDo eventDo = eventBetResult.getDynamicOperationEvent();
            // 异步上报、更新状态
            long eventAmount = betRecordDo.getPayOutAmount() - betRecordDo.getPayAmount();
            getDynamicOperationManager().asyncEventDetailUpdate(eventDo, betStatusEnum, detailId, resultDo.getBetId(), eventAmount);
            return this.buildResult(betRecordDo, resultDo);
        }
    }

    private BetRecordDo<R> loadBetRecordDo(BetDo<BD> betDo, GameEventDo<WD> gameEventDo) {
        //获取投注记录
        long betRecordId = generateBetRecordId();
        Pair<Long, Long> betAndPayAmount = this.getBetAndPayAmount(betDo);
        List<R> list = getGame().play(GamePlayDo.<WD>builder()
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(betAndPayAmount.getLeft())
                .payAmount(betAndPayAmount.getValue())
                .betRecordId(betRecordId)
                .gameEventDo(gameEventDo)
                .build());
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        // 构建投注记录
        return this.buildBetRecord(betRecordId, betDo, list);
    }

    private BetRecordDo<R> loadBetRecordDo(BetDo<BD> betDo) {
        return loadBetRecordDo(betDo, null);
    }

    private Pair<RedeemRewardResultDo, BetRecordDo<R>> rewardResult(BetDo<BD> betDo) {
        StarCardUseDo starCardUse = betDo.getStarCardUseDo();
        BetRecordDo<R> rBetRecordDo;
        int i = 0;
        RedeemRewardResultDo rewardResult;
        do {
            rBetRecordDo = loadBetRecordDo(betDo);
            rewardResult = bagService.use(starCardUse.getRewardId(), rBetRecordDo.getPayOutAmount(), rBetRecordDo.getId());
            i++;
        } while (rewardResult.getSuccess() || i > 10);
        return Pair.of(rewardResult, rBetRecordDo);
    }


    private Long saveEventDetail(UserInfo userInfo, Long recordId, BetRecordDo betRecordDo, GameEventDo eventDo) {
        // 扣减流水
        OperationEventDetailDo detailDo = new OperationEventDetailDo();
        detailDo.setId(recordId);
        detailDo.setEventRecordId(eventDo.getDynamicOperationEvent().getId());
        detailDo.setEventId(eventDo.getDynamicOperationEvent().getEventId());
        detailDo.setBetRecordId(betRecordDo.getId());
        detailDo.setBetAmount(betRecordDo.getPayAmount());
        detailDo.setPayOutAmount(betRecordDo.getPayOutAmount());
        detailDo.setEventAmount(betRecordDo.getPayOutAmount() - betRecordDo.getPayAmount());
        detailDo.setBetTime(betRecordDo.getCreateTime());
        detailDo.setPayOutTime(betRecordDo.getCreateTime());
        detailDo.setPlayerName(userInfo.getPlayerName());
        detailDo.setPlayerId(userInfo.getPlayerId());
        detailDo.setUserId(userInfo.getId());
        detailDo.setGameId(eventDo.getDynamicOperationEvent().getGameId());
        detailDo.setChannelId(eventDo.getDynamicOperationEvent().getChannelId());
        long currentMilli = DateUtil.getCurrentMilli();
        detailDo.setCreateTime(currentMilli);
        detailDo.setUpdateTime(currentMilli);
        detailDo.setBetStatus(BetStatusEnum.ING);
        return getDynamicOperationManager().addEventDetail(detailDo);
    }

    /**
     * 根据投注记录和单钱包返回结果
     * 构建返回结果
     *
     * @param rBetRecordDo
     * @param resultDo
     * @return
     */
    public abstract BetResultDo<BR> buildResult(BetRecordDo<R> rBetRecordDo, BetSingleWalletResultDo resultDo);

    /**
     * 构建投注记录
     *
     * @param betRecordId
     * @param betDo
     * @param list
     * @return
     */
    public abstract BetRecordDo<R> buildBetRecord(Long betRecordId, BetDo<BD> betDo, List<R> list);

    /**
     * 免费轮查询
     *
     * @param betDo
     * @return
     */
    public abstract BetResultDo<BR> betForFreeWheel(BetDo<BD> betDo);

    /**
     * 获取游戏投注金额和支付金额
     *
     * @param betDo
     * @return
     */
    public abstract Pair<Long, Long> getBetAndPayAmount(BetDo<BD> betDo);


    public abstract boolean hasRewardFreeWheel(List<R> gameRecordList);
}



