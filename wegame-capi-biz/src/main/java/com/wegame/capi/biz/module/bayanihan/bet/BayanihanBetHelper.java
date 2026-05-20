package com.wegame.capi.biz.module.bayanihan.bet;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.config.domain.BayanihanConfig;
import com.wegame.capi.biz.domain.biz.BayanihanBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.BayanihanGameRecordDo;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.base.AmountHelper.maxAmountControl;
import static com.wegame.capi.biz.module.base.AmountHelper.reachMaxMultiple;

@Component
public class BayanihanBetHelper implements BetHelper<BayanihanBetRecordDetailDo, BayanihanGameRecordDo> {

    @Autowired
    BayanihanConfig bayanihanConfig;

    @Override
    public BetRecordDo<BayanihanGameRecordDo> buildBetRecord(long betRecordId, BetDo betDo, List<BayanihanGameRecordDo> gameRecordList) {
        //// 总中奖金额
        GameRecordCalcResult result = calcRewardAmount(gameRecordList);
        //计算实际金额
        long actualRewardAmount = result.getTotalRewardAmount();
        BetRecordDo<BayanihanGameRecordDo> res =  BetRecordBuilder.build(betRecordId,
                betDo,
                maxAmountControl(actualRewardAmount, betDo.getBetAmount(), bayanihanConfig.getRewardMaxMultiple()),
                reachMaxMultiple(actualRewardAmount, betDo.getBetAmount(), bayanihanConfig.getRewardMaxMultiple()),
                null,
                gameRecordList);
        res.setFreeGameInfo(BetRecordDo.FreeGameInfo.builder()
                .initialFreeGameCount(result.fgInitialCount).initialMultiple(result.fgInitialMultiple).finalMultiple(result.fgFinalMultiple)
                .build());
        return res;
    }

    @Override
    public BetResultDo<BayanihanBetRecordDetailDo> handleBetResult(BetRecordDo<BayanihanGameRecordDo> betRecord, SingleWalletBetResultDo result) {
        AssertUtil.assertNotNull(result, ErrorCode.ERR_INTER);

        if (Objects.equals(result.getBetStatus(), BetStatusEnum.SUCCESS)) {
            List<BayanihanGameRecordDo> firstBatchList = getFirstBatchGameRecordList(betRecord.getGameRecordList());

            // 4、组装返回结果
            //// 合计中奖总金额
            long overallRewardAmount = firstBatchList.stream()
                    .filter(item -> Objects.nonNull(item.getRewardResult()))
                    .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                    .sum();
            BetRecordDo.FreeGameInfo freeGameInfo = betRecord.getFreeGameInfo();
            BayanihanBetRecordDetailDo.FreeGameInfoDo freeGameInfoDo = Objects.nonNull(freeGameInfo) ?
                    BayanihanBetRecordDetailDo.FreeGameInfoDo.builder().initialMultiple(freeGameInfo.getInitialMultiple())
                            .finalMultiple(freeGameInfo.getFinalMultiple())
                            .freeWheelCount(freeGameInfo.getInitialFreeGameCount())
                            .build() : null;
            return BetResultDo.<BayanihanBetRecordDetailDo>builder()
                    .betRecordId(betRecord.getId())
                    .gameId(betRecord.getGameId())
                    .betType(BetTypeEnum.valueOfCode(betRecord.getBetType()))
                    .betSubType(BetSubTypeEnum.valueOfCode(betRecord.getBetSubType()))
                    .credit(result.getCredit())
                    .overallRewardAmount(maxAmountControl(overallRewardAmount, betRecord.getBetAmount(), bayanihanConfig.getRewardMaxMultiple()))
                    .totalPayoutAmount(betRecord.getPayOutAmount())
                    .resultDetail(BayanihanBetRecordDetailDo.builder()
                            .allWheelRewardSubType(RewardHelper.calculateRewardSubType(betRecord.getBetAmount(), overallRewardAmount))
                            .gameRecordList(firstBatchList)
                            .freeGameInfo(freeGameInfoDo)
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

    public List<BayanihanGameRecordDo> getFirstBatchGameRecordList(List<BayanihanGameRecordDo> list) {
        Long batchId = list.get(0).getBatchId();
        return list.stream()
                .filter(item -> Objects.equals(batchId, item.getBatchId()))
                .collect(Collectors.toList());
    }

    public List<BayanihanGameRecordDo> getFreeBatchGameRecordList(List<BayanihanGameRecordDo> list) {
        Long batchId = list.get(0).getBatchId();
        return list.stream()
                .filter(item -> !Objects.equals(batchId, item.getBatchId()))
                .collect(Collectors.toList());
    }

    public GameRecordCalcResult calcRewardAmount(List<BayanihanGameRecordDo> gameRecordList) {
        GameRecordCalcResult.GameRecordCalcResultBuilder builder = GameRecordCalcResult.builder();
        long totalRewardAmount = 0L;
        List<BayanihanGameRecordDo> firstBatchList = getFirstBatchGameRecordList(gameRecordList);
        long mgRewardAmount = calculateRewardAmount(firstBatchList);
        builder.mgRewardAmount(mgRewardAmount);
        totalRewardAmount += mgRewardAmount;
        if (hasRewardFreeWheel(firstBatchList)) {

            //初始轮
            BayanihanGameRecordDo firstBatchLastRecord = firstBatchList.get(firstBatchList.size() - 1);
            builder.fgInitialMultiple(firstBatchLastRecord.getCollectionInfo().getCurrentMultiple());
            builder.fgInitialCount(firstBatchLastRecord.getRewardResult().getRewardFreeWheelTimes());

            // 免费轮总金额 * 最终倍率
            List<BayanihanGameRecordDo> freeGameList = getFreeBatchGameRecordList(gameRecordList);
            BigDecimal fgFinalMultiple = freeGameList.stream()
                    .map(record -> record.getCollectionInfo().getCurrentMultiple())
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            builder.fgFinalMultiple(fgFinalMultiple);
            // 免费轮基础中奖金额
            long fgBaseRewardAmount = calculateRewardAmount(freeGameList);
            builder.fgBaseRewardAmount(fgBaseRewardAmount);
            // 免费轮实际
            long freeGameRewardAmount = BigDecimal.valueOf(fgBaseRewardAmount).multiply(fgFinalMultiple).longValue();
            builder.fgRewardAmount(freeGameRewardAmount);
            totalRewardAmount += freeGameRewardAmount;
        }
        builder.totalRewardAmount(totalRewardAmount);
        return builder.build();
    }

    private long calculateRewardAmount(List<BayanihanGameRecordDo> list) {
        return list.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
    }

    @Override
    public boolean hasRewardFreeWheel(List<BayanihanGameRecordDo> gameRecordList) {
        BayanihanGameRecordDo gameRecord = gameRecordList.stream()
                .filter(item -> item.getRewardResult() != null && item.getRewardResult().isHasRewardFreeWheel())
                .findFirst()
                .orElse(null);
        return Objects.nonNull(gameRecord);
    }

    @Data
    @NoArgsConstructor
    @SuperBuilder
    public static class GameRecordCalcResult {
        /**
         * 总派奖
         */
        private long totalRewardAmount;

        /**
         * 首轮派奖
         */
        private long mgRewardAmount;

        /**
         * 免费轮初始轮数
         */
        private int fgInitialCount;

        /**
         * 免费轮派奖金额
         */
        private long fgRewardAmount;

        /**
         * 免费轮基础金额
         */
        private long fgBaseRewardAmount;

        /**
         * 免费轮初始倍率
         */
        private BigDecimal fgInitialMultiple;

        /**
         * 免费轮最终倍率
         */
        private BigDecimal fgFinalMultiple;
    }
}
