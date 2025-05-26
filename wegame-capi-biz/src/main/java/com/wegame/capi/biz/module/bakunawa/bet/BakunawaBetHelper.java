package com.wegame.capi.biz.module.bakunawa.bet;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.BakunawaGameRecordDo;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class BakunawaBetHelper implements BetHelper<BakunawaGameRecordDo, BakunawaGameRecordDo> {

    @Override
    public BetRecordDo<BakunawaGameRecordDo> buildBetRecord(long betRecordId,
                                                            BetDo betDo,
                                                            List<BakunawaGameRecordDo> gameRecordList) {
        // 构建投注记录
        long totalRewardAmount = gameRecordList.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        return BetRecordBuilder.build(betRecordId,
                betDo,
                totalRewardAmount,
                false,
                null,
                gameRecordList);
    }

    @Override
    public BetResultDo<BakunawaGameRecordDo> handleBetResult(BetRecordDo<BakunawaGameRecordDo> betRecord, SingleWalletBetResultDo resultDo) {
        AssertUtil.assertNotNull(betRecord, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        BakunawaGameRecordDo gameRecord = betRecord.getGameRecordList().get(0);
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.SUCCESS)) {
            // 组装返回结果
            return BetResultDo.<BakunawaGameRecordDo>builder()
                    .betRecordId(betRecord.getId())
                    .gameId(betRecord.getGameId())
                    .betType(BetTypeEnum.valueOfCode(betRecord.getBetType()))
                    .betSubType(BetSubTypeEnum.valueOfCode(betRecord.getBetSubType()))
                    .credit(resultDo.getCredit())
                    .overallRewardAmount(Objects.nonNull(gameRecord.getRewardResult()) ? gameRecord.getRewardResult().getTotalRewardAmount() : 0L)
                    .totalPayoutAmount(betRecord.getPayOutAmount())
                    .resultDetail(gameRecord)
                    .build();
        } else if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.FAIL)) {
            if (resultDo.getErrorCode() != null) {
                throw new BusinessException(resultDo.getErrorCode());
            }
            throw new BusinessException(ErrorCode.ERR_INTER);
        } else if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.ING)) {
            throw new BusinessException(ErrorCode.ERR_NET);
        } else {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
    }

    @Override
    public boolean hasRewardFreeWheel(List<BakunawaGameRecordDo> gameRecordList) {
        return false;
    }
}
