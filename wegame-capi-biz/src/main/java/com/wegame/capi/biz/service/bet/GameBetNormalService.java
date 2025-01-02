package com.wegame.capi.biz.service.bet;

import com.google.common.collect.Lists;
import com.wegame.capi.biz.domain.business.bet.*;

import org.springframework.stereotype.Service;


/**
 * 正常投注
 */
@Service
public class GameBetNormalService extends AbsGameBetService<GameBetNormalReqDo, GameBetNormalResultDo> {

    public GameBetResultDo<GameBetNormalResultDo> bet(GameBetReqDo<GameBetNormalReqDo> betReqDo) {
        //获取游戏结果
        GameBetResultDetailDo resultDetail = super.getResultDetail(betReqDo.getGameId(), betReqDo.getBetType(), betReqDo.getBetSubType(), betReqDo.getBetDetail().getBetAmount());
        //生成投注结果
        GameBetRecordDo betRecord = super.getBetRecord(betReqDo, resultDetail.getBetAmount(), resultDetail.getRewardAmount(), Lists.newArrayList(resultDetail));
        //保存投注记录并派奖
        super.saveBetRecordAndPayOut(betRecord);
        //组装返回结果
        return GameBetResultDo.<GameBetNormalResultDo>builder()
                .gameRecordId(betRecord.getId())
                .gameId(betReqDo.getGameId())
                .betType(betReqDo.getBetType())
                .betSubType(betReqDo.getBetSubType())
                .resultDetail(GameBetNormalResultDo.builder()
                        .resultDetailDo(resultDetail).build())
                .build();

    }

}
