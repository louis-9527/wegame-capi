package com.wegame.capi.biz.module.mangkukulam.bet;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.config.domain.MangkukulamConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MangkukulamBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.biz.module.mangkukulam.MangkukulamCalcUtil;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.base.AmountHelper.maxAmountControl;
import static com.wegame.capi.biz.module.base.AmountHelper.reachMaxMultiple;

@Slf4j
@Component
public class MangkukulamBetHelper implements BetHelper<MangkukulamBetRecordDetailDo, MangkukulamGameRecordDo> {

    @Autowired
    MangkukulamConfig mangkukulamConfig;

    @Override
    public BetRecordDo<MangkukulamGameRecordDo> buildBetRecord(long betRecordId, BetDo betDo, List<MangkukulamGameRecordDo> gameRecordList) {
        //// 总中奖金额
        long totalRewardAmount = gameRecordList.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        return BetRecordBuilder.build(betRecordId,
                betDo,
                maxAmountControl(totalRewardAmount, betDo.getBetAmount(), mangkukulamConfig.getRewardMaxMultiple()),
                reachMaxMultiple(totalRewardAmount, betDo.getBetAmount(), mangkukulamConfig.getRewardMaxMultiple()),
                null,
                gameRecordList
        );
    }

    @Override
    public BetResultDo<MangkukulamBetRecordDetailDo> handleBetResult(BetRecordDo<MangkukulamGameRecordDo> betRecord, SingleWalletBetResultDo result) {
        AssertUtil.assertNotNull(result, ErrorCode.ERR_INTER);
        if (Objects.equals(result.getBetStatus(), BetStatusEnum.SUCCESS)) {
            List<MangkukulamGameRecordDo> firstBatchList = getFirstBatchGameRecordList(betRecord.getGameRecordList());
            // 4、组装返回结果
            //// 合计中奖总金额
            long overallRewardAmount = MangkukulamCalcUtil.calcTotalRewardAmount(firstBatchList);
            // 计算中奖
            return BetResultDo.<MangkukulamBetRecordDetailDo>builder()
                    .betRecordId(betRecord.getId())
                    .gameId(betRecord.getGameId())
                    .betType(BetTypeEnum.valueOfCode(betRecord.getBetType()))
                    .betSubType(BetSubTypeEnum.valueOfCode(betRecord.getBetSubType()))
                    .credit(result.getCredit())
                    .overallRewardAmount(maxAmountControl(overallRewardAmount, betRecord.getBetAmount(), mangkukulamConfig.getRewardMaxMultiple()))
                    .totalPayoutAmount(betRecord.getPayOutAmount())
                    .resultDetail(MangkukulamBetRecordDetailDo.builder()
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

    public BetResultDo<MangkukulamBetRecordDetailDo> handleBetResult(BetRecordDo<MangkukulamGameRecordDo> betRecord,
                                                                      List<MangkukulamGameRecordDo> list,
                                                                      Long credit) {
        List<MangkukulamGameRecordDo> firstBatchList = getFirstBatchGameRecordList(list);
        MangkukulamGameRecordDo lastGameRecord = firstBatchList.get(firstBatchList.size() - 1);
        // 4、组装返回结果
        long overallRewardAmount = MangkukulamCalcUtil.calcTotalRewardAmount(firstBatchList);
        return BetResultDo.<MangkukulamBetRecordDetailDo>builder()
                .betRecordId(betRecord.getId())
                .gameId(betRecord.getGameId())
                .betType(BetTypeEnum.valueOfCode(betRecord.getBetType()))
                .betSubType(BetSubTypeEnum.valueOfCode(betRecord.getBetSubType()))
                .credit(credit)
                .overallRewardAmount(maxAmountControl(overallRewardAmount, betRecord.getBetAmount(), mangkukulamConfig.getRewardMaxMultiple()))
                .totalPayoutAmount(betRecord.getPayOutAmount())
                .resultDetail(MangkukulamBetRecordDetailDo.builder()
                        .allWheelRewardSubType(RewardHelper.calculateRewardSubType(betRecord.getBetAmount(), overallRewardAmount))
                        .gameRecordList(null)
                        .volatilityGameRecordIds(lastGameRecord.isLastGameFlag() ? null : lastGameRecord.getGameRecordIds())
                        .build())
                .build();
    }


    @Override
    public boolean hasRewardFreeWheel(List<MangkukulamGameRecordDo> gameRecordList) {
        MangkukulamGameRecordDo gameRecord = gameRecordList.stream()
                .filter(item -> item.getRewardResult() != null && item.getRewardResult().isHasRewardFreeWheel())
                .findFirst()
                .orElse(null);
        return Objects.nonNull(gameRecord);
    }

    public List<MangkukulamGameRecordDo> getFirstBatchGameRecordList(List<MangkukulamGameRecordDo> list) {
        Long batchId = list.get(0).getBatchId();
        return list.stream()
                .filter(item -> Objects.equals(batchId, item.getBatchId()))
                .collect(Collectors.toList());
    }
}
