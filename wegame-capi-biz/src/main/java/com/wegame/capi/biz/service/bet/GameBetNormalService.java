package com.wegame.capi.biz.service.bet;

import com.google.common.collect.Lists;
import com.wegame.capi.biz.domain.business.bet.*;
import com.wegame.capi.biz.service.GamePlayService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 正常投注
 */
@Service
public class GameBetNormalService extends AbsGameBetService<GameBetNormalReqDo, GameBetResultDetailDo> {
    @Autowired
    private GamePlayService gamePlayService;

    public GameBetResultDo<GameBetResultDetailDo> bet(GameBetReqDo<GameBetNormalReqDo> betReqDo) {
        //获取轮盘结果
        Integer[][] sysBol = gamePlayService.getSysBol(betReqDo.getGameId());
        //获取游戏结果
        GameBetResultDetailDo resultDetail = super.getResultDetail(0, betReqDo.getBetDetail().getBetAmount(), sysBol);
        //获取投注结果
        GameBetRecordDo betRecord = super.getBetRecord(betReqDo.getGameId(), resultDetail.getBetAmount(), resultDetail.getRewardAmount(), Lists.newArrayList(resultDetail));
        //保存投注记录并派奖
        super.saveBetRecordAndPayOut(betRecord);
        //组装返回结果

        return GameBetResultDo.<GameBetResultDetailDo>builder()
                .gameRecordId(betRecord.getId())
                .gameId(betReqDo.getGameId())
                .betType(betReqDo.getBetType())
                .betSubType(betReqDo.getBetSubType())
                .resultDetail(resultDetail)
                .build();

    }

    @Override
    public Class<GameBetNormalReqDo> tClass() {
        return GameBetNormalReqDo.class;
    }

}
