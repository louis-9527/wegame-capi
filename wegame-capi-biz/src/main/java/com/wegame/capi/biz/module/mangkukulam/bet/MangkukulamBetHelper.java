package com.wegame.capi.biz.module.mangkukulam.bet;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.biz.config.domain.MangkukulamConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MangkukulamBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MangkukulamBetHelper implements BetHelper<MangkukulamBetRecordDetailDo, MangkukulamGameRecordDo> {

    @Autowired(required = false)
    MangkukulamConfig mangkukulamConfig;

    @Override
    public BetRecordDo<MangkukulamGameRecordDo> buildBetRecord(long betRecordId, BetDo betDo, List<MangkukulamGameRecordDo> gameRecordList) {
        //// 总中奖金额
//        long totalRewardAmount = gameRecordList.stream()
//                .filter(item -> Objects.nonNull(item.getRewardResult()))
//                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
//                .sum();
//        return BetRecordBuilder.build(betRecordId,
//                betDo,
//                maxAmountControl(totalRewardAmount, betDo.getBetAmount(), lamangConfig.getRewardMaxMultiple()),
//                reachMaxMultiple(totalRewardAmount, betDo.getBetAmount(), lamangConfig.getRewardMaxMultiple()),
//                buildBetCommission(betDo),
//                gameRecordList
//        );
        throw new BusinessException(ErrorCode.ERR_INTER, "todo");
    }

    @Override
    public BetResultDo<MangkukulamBetRecordDetailDo> handleBetResult(BetRecordDo<MangkukulamGameRecordDo> betRecord, SingleWalletBetResultDo result) {
        throw new BusinessException(ErrorCode.ERR_INTER, "todo");
    }

    @Override
    public boolean hasRewardFreeWheel(List<MangkukulamGameRecordDo> gameRecordList) {
        throw new BusinessException(ErrorCode.ERR_INTER, "todo");
    }
}
