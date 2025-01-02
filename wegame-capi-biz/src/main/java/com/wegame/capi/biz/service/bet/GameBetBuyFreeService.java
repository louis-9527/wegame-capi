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
        resultDetails.add(betResult);
        long rewardAmount = 0L;
        List<Integer> freeSpinIds = new ArrayList<>(resultDetails.size());
        for (GameBetResultDetailDo resultDetail : resultDetails) {
            rewardAmount = rewardAmount + resultDetail.getRewardAmount();
            freeSpinIds.add(resultDetail.getId());
        }

        //保存投注记录并派奖
        GameBetRecordDo betRecord = super.getBetRecord(req.getGameId(), req.getBetDetail().getBuyAmount(), rewardAmount, resultDetails);
        super.saveBetRecordAndPayOut(betRecord);
        return GameBetResultDo.<GameBetBuyResultDo>builder()
                .gameId(req.getGameId())
                .betType(req.getBetType())
                .betSubType(req.getBetSubType())
                .resultDetail(GameBetBuyResultDo.builder()
                        .freeSpinIds(freeSpinIds)
                        .resultDetailDo(betResult)
                        .build())
                .build();
    }

    @Override
    public Class<GameBetBuyReqDo> tClass() {
        return GameBetBuyReqDo.class;
    }

    private int getRandomFreeSpins() {
        Random random = new Random();
        int rand = random.nextInt(100) + 1; // 生成1到100之间的随机数

        if (rand <= 50) {
            return 10; // 50%的概率
        } else if (rand <= 85) {
            return 11; // 35%的概率
        } else if (rand <= 95) {
            return 12; // 10%的概率
        } else {
            return 13; // 5%的概率
        }
    }
}
