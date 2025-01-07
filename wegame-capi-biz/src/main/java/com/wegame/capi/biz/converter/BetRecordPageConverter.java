package com.wegame.capi.biz.converter;

import com.wegame.capi.biz.builder.SymbolBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MariaMakilingBetRecordDetailDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.client.domain.vo.BetRecordDetailVo;
import com.wegame.capi.client.domain.vo.BetRecordPageVo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * BetRecordConverter
 *
 * @author generate
 */
public class BetRecordPageConverter {
    public static BetRecordPageVo doToPageVo(BetRecordDo betRecordDo) {
        if (Objects.isNull(betRecordDo)) {
            return null;
        }
        BetRecordPageVo vo = new BetRecordPageVo();
        vo.setId(Objects.nonNull(betRecordDo.getId()) ? String.valueOf(betRecordDo.getId()) : null);
        vo.setPId(betRecordDo.getPId());
        vo.setChannelId(betRecordDo.getChannelId());
        vo.setUserId(Objects.nonNull(betRecordDo.getUserId()) ? String.valueOf(betRecordDo.getUserId()) : null);
        vo.setPlayerName(betRecordDo.getPlayerName());
        vo.setBetType(betRecordDo.getBetType());
        vo.setBetSubType(betRecordDo.getBetSubType());
        vo.setBetId(betRecordDo.getBetId());
        vo.setBetSerialNo(betRecordDo.getBetSerialNo());
        vo.setBetAmount(Objects.nonNull(betRecordDo.getBetAmount()) ? String.valueOf(betRecordDo.getBetAmount()) : null);
        vo.setPayOutAmount(Objects.nonNull(betRecordDo.getPayOutAmount()) ? String.valueOf(betRecordDo.getPayOutAmount()) : null);
        vo.setStatus(betRecordDo.getStatus());
        vo.setCreateTime(betRecordDo.getCreateTime());
        return vo;
    }

    public static List<BetRecordPageVo> doToPageVoList(List<BetRecordDo> doList) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BetRecordPageConverter::doToPageVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static BetRecordPageVo doToPageDetailVo(BetRecordDo betRecordDo) {
        if (Objects.isNull(betRecordDo)) {
            return null;
        }
        // MariaMakilingBetRecordDetailDo
        MariaMakilingBetRecordDetailDo detail = (MariaMakilingBetRecordDetailDo) betRecordDo.getDetail();
        BetRecordPageVo vo = new BetRecordPageVo();
        vo.setId(Objects.nonNull(betRecordDo.getId()) ? String.valueOf(betRecordDo.getId()) : null);
        vo.setPId(betRecordDo.getPId());
        vo.setChannelId(betRecordDo.getChannelId());
        vo.setUserId(Objects.nonNull(betRecordDo.getUserId()) ? String.valueOf(betRecordDo.getUserId()) : null);
        vo.setPlayerName(betRecordDo.getPlayerName());
        vo.setBetType(betRecordDo.getBetType());
        vo.setBetSubType(betRecordDo.getBetSubType());
        vo.setBetId(betRecordDo.getBetId());
        vo.setBetSerialNo(betRecordDo.getBetSerialNo());
        vo.setBetAmount(Objects.nonNull(betRecordDo.getBetAmount()) ? String.valueOf(betRecordDo.getBetAmount()) : null);
        vo.setPayOutAmount(Objects.nonNull(betRecordDo.getPayOutAmount()) ? String.valueOf(betRecordDo.getPayOutAmount()) : null);
        vo.setStatus(betRecordDo.getStatus());
        vo.setCreateTime(betRecordDo.getCreateTime());
        vo.setDetailVoList(detailDoListToDetailVoList(detail.getGameRecordList()));
        return vo;
    }

    public static BetRecordDetailVo detailDoToDetailVo(MariaMakilingGameRecordDo recordDetailDo) {
        if (Objects.isNull(recordDetailDo)) {
            return null;
        }
        BetRecordDetailVo vo = new BetRecordDetailVo();
        vo.setGameResult(SymbolBuilder.build(recordDetailDo.getWheel().getSymbolArr()));
        vo.setRewardAmount(recordDetailDo.getRewardResult().getTotalRewardAmount());
        vo.setMultiple(recordDetailDo.getRewardResult().getTotalMultiple());
        return vo;
    }

    public static List<BetRecordDetailVo> detailDoListToDetailVoList(List<MariaMakilingGameRecordDo> gameRecordList) {
        return Optional.ofNullable(gameRecordList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BetRecordPageConverter::detailDoToDetailVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}