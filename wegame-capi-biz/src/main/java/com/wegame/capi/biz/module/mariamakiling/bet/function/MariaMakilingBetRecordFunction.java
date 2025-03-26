package com.wegame.capi.biz.module.mariamakiling.bet.function;

import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.module.base.bet.function.BetRecordFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.wegame.capi.biz.module.base.AmountHelper.maxAmountControl;
import static com.wegame.capi.biz.module.base.AmountHelper.reachMaxMultiple;

@Component
public class MariaMakilingBetRecordFunction implements BetRecordFunction<MariaMakilingGameRecordDo> {

    @Autowired
    MariaMakilingConfig mariaMakilingConfig;

    @Override
    public BetRecordDo<MariaMakilingGameRecordDo> build(long betRecordId, BetDo betDo, List<MariaMakilingGameRecordDo> gameRecordList) {
        // 构建投注记录
        long totalRewardAmount = gameRecordList.stream()
                .filter(item -> Objects.nonNull(item.getRewardResult()))
                .mapToLong(item -> item.getRewardResult().getTotalRewardAmount())
                .sum();
        return BetRecordBuilder.build(betRecordId,
                betDo,
                maxAmountControl(totalRewardAmount, betDo.getBetAmount(), mariaMakilingConfig.getRewardMaxMultiple()),
                reachMaxMultiple(totalRewardAmount, betDo.getBetAmount(), mariaMakilingConfig.getRewardMaxMultiple()),
                gameRecordList);
    }
}
