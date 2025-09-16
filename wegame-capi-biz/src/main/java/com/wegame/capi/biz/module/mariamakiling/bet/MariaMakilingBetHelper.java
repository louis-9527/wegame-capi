package com.wegame.capi.biz.module.mariamakiling.bet;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.wegame.capi.biz.module.base.AmountHelper.maxAmountControl;
import static com.wegame.capi.biz.module.base.AmountHelper.reachMaxMultiple;

@Component
public class MariaMakilingBetHelper implements BetHelper<MariaMakilingGameRecordDo, MariaMakilingGameRecordDo> {

    @Autowired
    MariaMakilingConfig mariaMakilingConfig;

    @Override
    public BetRecordDo<MariaMakilingGameRecordDo> buildBetRecord(long betRecordId,
                                                                 BetDo betDo,
                                                                 List<MariaMakilingGameRecordDo> gameRecordList,
                                                                 GamePlayTypeEnum gamePlayType) {
        // 构建投注记录
        long totalRewardAmount = gameRecordList.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        return BetRecordBuilder.build(betRecordId,
                betDo,
                maxAmountControl(totalRewardAmount, betDo.getBetAmount(), mariaMakilingConfig.getRewardMaxMultiple()),
                reachMaxMultiple(totalRewardAmount, betDo.getBetAmount(), mariaMakilingConfig.getRewardMaxMultiple()),
                null,
                gameRecordList,
                gamePlayType);
    }

    @Override
    public BetResultDo<MariaMakilingGameRecordDo> handleBetResult(BetRecordDo<MariaMakilingGameRecordDo> betRecord, SingleWalletBetResultDo resultDo) {
        AssertUtil.assertNotNull(betRecord, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        MariaMakilingGameRecordDo gameRecord = betRecord.getGameRecordList().get(0);
        if (Objects.equals(resultDo.getBetStatus(), BetStatusEnum.SUCCESS)) {
            return BetResultDo.<MariaMakilingGameRecordDo>builder()
                    .betStatus(BetStatusEnum.SUCCESS)
                    .betRecordId(betRecord.getId())
                    .gameId(betRecord.getGameId())
                    .betType(BetTypeEnum.valueOfCode(betRecord.getBetType()))
                    .betSubType(BetSubTypeEnum.valueOfCode(betRecord.getBetSubType()))
                    .credit(resultDo.getCredit())
                    .overallRewardAmount(gameRecord.getOverallRewardAmount())
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
    public boolean hasRewardFreeWheel(List<MariaMakilingGameRecordDo> gameRecordList) {
        if (CollectionUtils.isEmpty(gameRecordList)) {
            return false;
        }
        MariaMakilingGameRecordDo gameRecord = gameRecordList.stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getRewardResult() != null && item.getRewardResult().isHasRewardFreeWheel())
                .findFirst()
                .orElse(null);
        return Objects.nonNull(gameRecord);
    }
}
