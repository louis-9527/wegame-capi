package com.wegame.capi.biz.module.kapre.bet;

import com.wegame.capi.biz.config.domain.KapreConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.KapreBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.KapreGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.module.base.bet.BetHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class KapreBetHelper implements BetHelper<KapreBetRecordDetailDo, KapreGameRecordDo> {

    @Autowired
    KapreConfig kapreConfig;

    @Override
    public BetRecordDo<KapreGameRecordDo> buildBetRecord(long betRecordId, BetDo betDo, List<KapreGameRecordDo> gameRecordList) {
        throw new RuntimeException("todo");
    }

    @Override
    public BetResultDo<KapreBetRecordDetailDo> handleBetResult(BetRecordDo<KapreGameRecordDo> betRecord, SingleWalletBetResultDo result) {
        throw new RuntimeException("todo");
    }

    private List<MalakasGameRecordDo> getFirstBatchGameRecordList(List<MalakasGameRecordDo> list) {
        Long batchId = list.get(0).getBatchId();
        return list.stream()
                .filter(item -> Objects.equals(batchId, item.getBatchId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasRewardFreeWheel(List<KapreGameRecordDo> gameRecordList) {
        KapreGameRecordDo gameRecord = gameRecordList.stream()
                .filter(item -> item.getRewardResult() != null && item.getRewardResult().isHasRewardFreeWheel())
                .findFirst()
                .orElse(null);
        return Objects.nonNull(gameRecord);
    }
}
