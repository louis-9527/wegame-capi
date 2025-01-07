package com.wegame.capi.biz.converter;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.biz.builder.SymbolBuilder;
import com.wegame.capi.biz.domain.biz.MariaMakilingBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.client.domain.vo.BetRecordDetailVo;
import com.wegame.capi.client.domain.vo.BetRecordGameDetailVo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * BetRecordConverter
 *
 * @author generate
 */
public class BetRecordDetailConverter {
    public static BetRecordDetailVo doToVo(MariaMakilingBetRecordDetailDo detailDo) {
        if (Objects.isNull(detailDo) || CollectionUtil.isEmpty(detailDo.getGameRecordList())) {
            return null;
        }
        BetRecordDetailVo vo = new BetRecordDetailVo();
        vo.setGameDetailVoList(detailDoListToDetailVoList(detailDo.getGameRecordList()));
        return vo;
    }

    public static List<BetRecordGameDetailVo> detailDoListToDetailVoList(List<MariaMakilingGameRecordDo> gameRecordList) {
        return Optional.ofNullable(gameRecordList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BetRecordDetailConverter::detailDoToDetailVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static BetRecordGameDetailVo detailDoToDetailVo(MariaMakilingGameRecordDo gameRecordDo) {
        if (Objects.isNull(gameRecordDo)) {
            return null;
        }
        BetRecordGameDetailVo vo = new BetRecordGameDetailVo();
        vo.setGameResult(SymbolBuilder.build(gameRecordDo.getWheel().getSymbolArr()));
        vo.setRewardAmount(Optional.ofNullable(gameRecordDo.getRewardResult()).map(RewardResultDo::getTotalRewardAmount).orElse(0L));
        vo.setMultiple(Optional.ofNullable(gameRecordDo.getRewardResult()).map(RewardResultDo::getTotalMultiple).orElse(null));
        return vo;

    }


}