package com.wegame.capi.biz.module.base.bet.strategy;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.lock.DistributeLock;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.promotion.BagApi;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.BetSingleWalletResultDo;
import com.wegame.capi.biz.domain.biz.bet.StarCardUseDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.promotion.RedeemRewardResultDo;
import com.wegame.capi.biz.domain.biz.promotion.RewordDetailDo;
import com.wegame.capi.biz.domain.biz.promotion.StartCardUseRecordDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.service.promotion.BagService;
import com.wegame.capi.biz.service.promotion.StartCardUseRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.StartCardReportStatusEnum;
import com.wegame.capi.common.enums.StartCardUseStatusEnum;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Objects;

public abstract class AbstractBetPromotionStrategy<R extends ToVoConverter, BD, BR extends ToVoConverter, WD extends SymbolDo> extends AbstractBetStrategy<R, BD, BR> {

    @Autowired
    private StartCardUseRecordService startCardUseRecordService;
    @Autowired
    private BagService bagService;

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
        boolean useStartCard = betDo.getStarCardUseDo() != null;
        UserInfo userInfo = UserInfoContext.getUserInfo();
        // 生成投注记录
        BetRecordDo<R> rBetRecordDo = this.loadBetRecordDo(betDo);
        Pair<Long, Long> betAndPayAmount = this.getBetAndPayAmount(betDo);
        Long addId = null;
        if (useStartCard) {
            StarCardUseDo starCardUse = betDo.getStarCardUseDo();
            // 查询星卡详情
            RewordDetailDo rewordDetail = bagService.getByRewardConfigId(starCardUse.getRewardConfigId());
            AssertUtil.assertNotNull(rewordDetail, ErrorCode.ERR_PARAM);
            AssertUtil.assertTrue(Objects.equals(rewordDetail.getBetValue(), betAndPayAmount.getKey()), ErrorCode.ERR_PARAM);
            // 准备核销星卡
            StartCardUseRecordDo recordDo = StartCardUseRecordDo.builder()
                    .id(super.generateBetRecordId())
                    .gameId(this.getGame().getGameInfo().getId())
                    .betAmount(betAndPayAmount.getKey())
                    .payOutAmount(rBetRecordDo.getPayOutAmount())
                    .playerId(userInfo.getPlayerId())
                    .playName(userInfo.getPlayerName()).build();
            addId = startCardUseRecordService.add(recordDo, this.getGame().getGameInfo().getGameType());
            AssertUtil.assertNotNull(addId, ErrorCode.ERR_INTER);
            // 核销星卡
            RedeemRewardResultDo rewardResult = startCardUseRecordService.use(starCardUse.getRewardId(), rBetRecordDo.getPayOutAmount(), rBetRecordDo.getId());
            if (rewardResult.invalidPayOutAmount()) {
                // 无效的派奖金额,需要重新生成滚轮
                int i = 10;
                while (rewardResult.getSuccess() || i > 10) {
                    i++;
                    rBetRecordDo = this.loadBetRecordDo(betDo);
                    rewardResult = startCardUseRecordService.use(starCardUse.getRewardId(), rBetRecordDo.getPayOutAmount(), rBetRecordDo.getId());
                }
            }
            if (!rewardResult.getSuccess()) {
                // 更新星卡使用状态-失败
                StartCardUseRecordDo updateRecord = StartCardUseRecordDo.builder()
                        .id(addId)
                        .useStatus(StartCardUseStatusEnum.FAIL)
                        .failMsg(rewardResult.getMessage())
                        .playName(userInfo.getPlayerName()).build();
                startCardUseRecordService.update(updateRecord, this.getGame().getGameInfo().getGameType());
                throw new BusinessException(ErrorCode.ERR_PARAM);
            }
            // 更新星卡使用状态-成功
            StartCardUseRecordDo updateRecord = StartCardUseRecordDo.builder()
                    .id(addId)
                    .useStatus(StartCardUseStatusEnum.SUCCESS)
                    .playName(userInfo.getPlayerName()).build();
            startCardUseRecordService.update(updateRecord, this.getGame().getGameInfo().getGameType());
        }
        //调用派奖
        BetSingleWalletResultDo resultDo = null;//saveBetRecordAndBetWithPayout(rBetRecordDo, rBetRecordDo.getGameRecordList().get(0).hasRewardFreeSpins());
        // 上报状态
        if (useStartCard) {
            AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
            if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.SUCCESS)) {
                // 更新星卡上报状态->无需上报
                StartCardUseRecordDo updateRecord = StartCardUseRecordDo.builder()
                        .id(addId)
                        .reportStatus(StartCardReportStatusEnum.NONE)
                        .playName(userInfo.getPlayerName()).build();
                startCardUseRecordService.update(updateRecord, this.getGame().getGameInfo().getGameType());
            } else {
                // todo 调用单钱包失败需要上报
            }
        }
        //组装返回结果
        return this.buildResult(rBetRecordDo, resultDo);
    }


    private BetRecordDo<R> loadBetRecordDo(BetDo<BD> betDo) {
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
                .build());
        AssertUtil.assertNotEmpty(list, ErrorCode.ERR_INTER);
        // 构建投注记录
        return this.buildBetRecord(betRecordId, betDo, list);
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


}
