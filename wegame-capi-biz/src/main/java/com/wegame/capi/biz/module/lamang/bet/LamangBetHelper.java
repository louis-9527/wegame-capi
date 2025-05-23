package com.wegame.capi.biz.module.lamang.bet;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.LamangBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.MalakasBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.LamangGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.base.AmountHelper.maxAmountControl;
import static com.wegame.capi.biz.module.base.AmountHelper.reachMaxMultiple;

@Component
public class LamangBetHelper implements BetHelper<LamangBetRecordDetailDo, LamangGameRecordDo> {

    //todo  切换config
    @Autowired
    MalakasConfig malakasConfig;

    @Override
    public BetRecordDo<LamangGameRecordDo> buildBetRecord(long betRecordId, BetDo betDo, List<LamangGameRecordDo> gameRecordList) {
        //// 总中奖金额
        long totalRewardAmount = gameRecordList.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        return BetRecordBuilder.build(betRecordId,
                betDo,
                maxAmountControl(totalRewardAmount, betDo.getBetAmount(), malakasConfig.getRewardMaxMultiple()),
                reachMaxMultiple(totalRewardAmount, betDo.getBetAmount(), malakasConfig.getRewardMaxMultiple()),
                null, // todo set jackpot @lyric
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
            long overallRewardAmount = firstBatchList.stream()
                    .filter(item -> Objects.nonNull(item.getRewardResult()))
                    .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                    .sum();
            return BetResultDo.<LamangBetRecordDetailDo>builder()
                    .betRecordId(betRecord.getId())
                    .gameId(betRecord.getGameId())
                    .betType(BetTypeEnum.valueOfCode(betRecord.getBetType()))
                    .betSubType(BetSubTypeEnum.valueOfCode(betRecord.getBetSubType()))
                    .credit(result.getCredit())
                    .overallRewardAmount(maxAmountControl(overallRewardAmount, betRecord.getBetAmount(), malakasConfig.getRewardMaxMultiple()))
                    .totalPayoutAmount(betRecord.getPayOutAmount())
                    .resultDetail(LamangBetRecordDetailDo.builder()
                            .allWheelRewardSubType(RewardHelper.calculateRewardSubType(betRecord.getBetAmount(), overallRewardAmount))
                            .gameRecordList(firstBatchList)
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

    private List<LamangGameRecordDo> getFirstBatchGameRecordList(List<LamangGameRecordDo> list) {
        Long batchId = list.get(0).getBatchId();
        return list.stream()
                .filter(item -> Objects.equals(batchId, item.getBatchId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasRewardFreeWheel(List<LamangGameRecordDo> gameRecordList) {
        LamangGameRecordDo gameRecord = gameRecordList.stream()
                .filter(item -> item.getRewardResult() != null && item.getRewardResult().isHasRewardFreeWheel())
                .findFirst()
                .orElse(null);
        return Objects.nonNull(gameRecord);
    }
}
