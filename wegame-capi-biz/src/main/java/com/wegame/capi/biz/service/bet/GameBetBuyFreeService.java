package com.wegame.capi.biz.service.bet;

import com.wegame.capi.biz.domain.business.bet.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class GameBetBuyFreeService extends AbsGameBetService<GameBetBuyReqDo, GameBetBuyResultDo> {

    @Override
    public GameBetResultDo<GameBetBuyResultDo> bet(GameBetReqDo<GameBetBuyReqDo> req) {

        //获取轮盘结果,必须包含免费轮
        GameBetResultDetailDo betResult = super.getResultDetail(req.getGameId(), req.getBetType(), req.getBetSubType(), req.getBetDetail().getBetAmount());
        //获取免费轮
        List<GameBetResultDetailDo> resultDetails = super.getResultDetail(req.getGameId(), req.getBetType(), req.getBetSubType(), req.getBetDetail().getBetAmount(), Boolean.TRUE);
        long rewardAmount = 0L;
        List<Integer> freeSpinIds = new ArrayList<>(resultDetails.size());
        for (GameBetResultDetailDo resultDetail : resultDetails) {
            if (resultDetail.getRewardAmount() != null) {
                rewardAmount = rewardAmount + resultDetail.getRewardAmount();
            }
            freeSpinIds.add(resultDetail.getId());
        }

        resultDetails.add(betResult);
        //保存投注记录并派奖
        GameBetRecordDo betRecord = super.getBetRecord(req, req.getBetDetail().getBuyAmount(), rewardAmount, resultDetails);
        super.saveBetRecordAndPayOut(betRecord);
        return GameBetResultDo.<GameBetBuyResultDo>builder()
                .gameRecordId(betRecord.getId())
                .gameId(req.getGameId())
                .betType(req.getBetType())
                .betSubType(req.getBetSubType())
                .resultDetail(GameBetBuyResultDo.builder()
                        .freeSpinIds(freeSpinIds)
                        .resultDetailDo(betResult)
                        .build())
                .build();
    }
}
