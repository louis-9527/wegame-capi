package com.wegame.capi.biz.converter;

import com.wegame.capi.biz.builder.SymbolBuilder;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.client.domain.vo.BetRecordVo;
import com.wegame.capi.client.domain.vo.mariamaking.MariaMakilingGameRecordVo;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MariaMakilingGameRecordConverter
 *
 * @author generate
 */
public class MariaMakilingGameRecordConverter {

    public static List<MariaMakilingGameRecordVo> doToVoList(List<MariaMakilingGameRecordDo> gameRecordList) {
        return Optional.ofNullable(gameRecordList)
                .orElse(Collections.emptyList())
                .stream()
                .map(MariaMakilingGameRecordConverter::doToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static MariaMakilingGameRecordVo doToVo(MariaMakilingGameRecordDo gameRecordDo) {
        if (Objects.isNull(gameRecordDo)) {
            return null;
        }
        return MariaMakilingGameRecordVo.builder()
                .index(gameRecordDo.getIndex())
                .betAmount(Optional.ofNullable(gameRecordDo.getRewardResult()).map(RewardResultDo::getBetAmount).orElse(0L))
                .rewardAmount(Optional.ofNullable(gameRecordDo.getRewardResult()).map(RewardResultDo::getTotalRewardAmount).orElse(0L))
                .wheel(SymbolBuilder.build(gameRecordDo.getWheel().getSymbolArr()))
                .multiple(Optional.ofNullable(gameRecordDo.getRewardResult()).map(RewardResultDo::getTotalMultiple).orElse(BigDecimal.ZERO))
                .build();
    }
}