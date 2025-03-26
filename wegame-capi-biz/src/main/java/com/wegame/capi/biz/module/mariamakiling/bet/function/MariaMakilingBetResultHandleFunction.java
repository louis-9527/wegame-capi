package com.wegame.capi.biz.module.mariamakiling.bet.function;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.module.base.bet.function.BetResultHandleFunction;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MariaMakilingBetResultHandleFunction implements BetResultHandleFunction<MariaMakilingGameRecordDo, MariaMakilingGameRecordDo> {

    @Override
    public BetResultDo<MariaMakilingGameRecordDo> handle(BetRecordDo<MariaMakilingGameRecordDo> betRecord, SingleWalletBetResultDo resultDo) {
        AssertUtil.assertNotNull(betRecord, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
        MariaMakilingGameRecordDo gameRecord = betRecord.getGameRecordList().get(0);
        AssertUtil.assertNotNull(resultDo, ErrorCode.ERR_INTER);
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
}
