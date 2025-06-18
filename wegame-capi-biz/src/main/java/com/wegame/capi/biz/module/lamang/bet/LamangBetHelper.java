package com.wegame.capi.biz.module.lamang.bet;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.LamangBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.LamangGameRecordDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotWinPrizeDo;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.biz.module.base.jackpot.AbsJackpotHandler;
import com.wegame.capi.biz.module.base.jackpot.JackpotHandlerContext;
import com.wegame.capi.biz.module.lamang.GameRecordCalcUtil;
import com.wegame.capi.biz.module.lamang.LamangJackpotHelper;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.base.AmountHelper.maxAmountControl;
import static com.wegame.capi.biz.module.base.AmountHelper.reachMaxMultiple;

@Slf4j
@Component
public class LamangBetHelper implements BetHelper<LamangBetRecordDetailDo, LamangGameRecordDo> {

    @Autowired
    LamangConfig lamangConfig;

    @Autowired
    JackpotHandlerContext jackpotHandlerContext;

    @Override
    public BetRecordDo<LamangGameRecordDo> buildBetRecord(long betRecordId, BetDo betDo, List<LamangGameRecordDo> gameRecordList) {
        //// 总中奖金额
        long totalRewardAmount = gameRecordList.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        return BetRecordBuilder.build(betRecordId,
                betDo,
                maxAmountControl(totalRewardAmount, betDo.getBetAmount(), lamangConfig.getRewardMaxMultiple()),
                reachMaxMultiple(totalRewardAmount, betDo.getBetAmount(), lamangConfig.getRewardMaxMultiple()),
                buildBetCommission(betDo),
                gameRecordList
        );
    }

    @Override
    public BetResultDo<LamangBetRecordDetailDo> handleBetResult(BetRecordDo<LamangGameRecordDo> betRecord, SingleWalletBetResultDo result) {
        AssertUtil.assertNotNull(result, ErrorCode.ERR_INTER);
        if (Objects.equals(result.getBetStatus(), BetStatusEnum.SUCCESS)) {
            List<LamangGameRecordDo> firstBatchList = getFirstBatchGameRecordList(betRecord.getGameRecordList());
            // 4、组装返回结果
            //// 合计中奖总金额
            long overallRewardAmount = GameRecordCalcUtil.calcTotalRewardAmount(firstBatchList);
            // 计算中奖
            JackpotWinPrizeDo jackpotWinPrizeDo = handleDrawJackpot(betRecord, result);
            return BetResultDo.<LamangBetRecordDetailDo>builder()
                    .betRecordId(betRecord.getId())
                    .gameId(betRecord.getGameId())
                    .betType(BetTypeEnum.valueOfCode(betRecord.getBetType()))
                    .betSubType(BetSubTypeEnum.valueOfCode(betRecord.getBetSubType()))
                    .credit(result.getCredit())
                    .overallRewardAmount(maxAmountControl(overallRewardAmount, betRecord.getBetAmount(), lamangConfig.getRewardMaxMultiple()))
                    .totalPayoutAmount(betRecord.getPayOutAmount())
                    .resultDetail(LamangBetRecordDetailDo.builder()
                            .allWheelRewardSubType(RewardHelper.calculateRewardSubType(betRecord.getBetAmount(), overallRewardAmount))
                            .gameRecordList(firstBatchList)
                            .jackpotRewardGameRecordId(Objects.nonNull(jackpotWinPrizeDo) ? jackpotWinPrizeDo.getBetGameRecordId() : null)
                            .build())
                    .build();
        } else if (Objects.equals(result.getBetStatus(), BetStatusEnum.FAIL)) {
            if (result.getErrorCode() != null) {
                throw new BusinessException(result.getErrorCode());
            }
            throw new BusinessException(ErrorCode.ERR_INTER);
        } else if (Objects.equals(result.getBetStatus(), BetStatusEnum.ING)) {
            throw new BusinessException(ErrorCode.ERR_NET);
        } else {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
    }

    @Override
    public boolean hasRewardFreeWheel(List<LamangGameRecordDo> gameRecordList) {
        LamangGameRecordDo gameRecord = gameRecordList.stream()
                .filter(item -> item.getRewardResult() != null && item.getRewardResult().isHasRewardFreeWheel())
                .findFirst()
                .orElse(null);
        return Objects.nonNull(gameRecord);
    }

    private JackpotWinPrizeDo handleDrawJackpot(BetRecordDo<LamangGameRecordDo> betRecord, SingleWalletBetResultDo singleWalletBetResultDo) {
        try {
            AbsJackpotHandler handler = jackpotHandlerContext.getHandler(JackpotTypeEnum.LAM_ANG);
            AssertUtil.assertNotNull(handler, ErrorCode.ERR_INTER);
            return handler.calculateWinPrize(LamangJackpotHelper.buildJackpotDrawAndAwardDo(betRecord, singleWalletBetResultDo));
        } catch (Exception e) {
            log.error("draw jackpot error,betRecordId:{}", betRecord.getId(), e);
        }
        return null;
    }

    private BetRecordDo.Jackpot buildBetCommission(BetDo betDo) {
        AbsJackpotHandler handler = jackpotHandlerContext.getHandler(JackpotTypeEnum.LAM_ANG);
        if (Objects.nonNull(handler)) {
            Map<JackpotRewardPoolTypeEnum, Long> commissiontMap = handler.calculateRewardPoolCommission(betDo.getBetPromotionType(), betDo.getBetType(), betDo.getBetAmount());
            return BetRecordDo.Jackpot.builder()
                    .grandContributionAmount(commissiontMap.get(JackpotRewardPoolTypeEnum.grand))
                    .majorContributionAmount(commissiontMap.get(JackpotRewardPoolTypeEnum.major))
                    .minorContributionAmount(commissiontMap.get(JackpotRewardPoolTypeEnum.minor))
                    .miniContributionAmount(commissiontMap.get(JackpotRewardPoolTypeEnum.mini))
                    .build();
        }
        return null;
    }

    private List<LamangGameRecordDo> getFirstBatchGameRecordList(List<LamangGameRecordDo> list) {
        Long batchId = list.get(0).getBatchId();
        return list.stream()
                .filter(item -> Objects.equals(batchId, item.getBatchId()))
                .collect(Collectors.toList());
    }
}
