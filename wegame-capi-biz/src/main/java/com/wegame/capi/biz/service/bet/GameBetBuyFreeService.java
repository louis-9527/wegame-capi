package com.wegame.capi.biz.service.bet;

import com.wegame.capi.biz.domain.business.bet.*;
import com.wegame.capi.biz.service.GamePlayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class GameBetBuyFreeService extends AbsGameBetService<GameBetBuyReqDo, GameBetBuyResultDo> {
    @Autowired
    private GamePlayService gamePlayService;

    @Override
    public GameBetResultDo<GameBetBuyResultDo> bet(GameBetReqDo<GameBetBuyReqDo> req) {

        //获取轮盘结果,必须包含免费轮
        Integer[][] sysBol = gamePlayService.getSysBol(req.getGameId());
        //获取游戏结果
        List<GameBetResultDetailDo> resultDetails = new ArrayList<>();

        GameBetResultDetailDo resultDetail = super.getResultDetail(-1, req.getBetDetail().getBetAmount(), sysBol);
        resultDetails.add(resultDetail);
        //获取免费轮盘
        List<Integer[][]> symBols = gamePlayService.getSysBolExcludeScatter(req.getGameId(), this.getRandomFreeSpins());
        long rewardAmount = 0L;
        //获取游戏结果
        List<Integer> freeSpinIds = new ArrayList<>(symBols.size());
        for (int i = 0; i < symBols.size(); i++) {
            freeSpinIds.add(i);
            Integer[][] symBol = symBols.get(i);
            //获取游戏结果
            GameBetResultDetailDo freeResultDetail = super.getResultDetail(i, req.getBetDetail().getBetAmount(), symBol);
            rewardAmount = rewardAmount + resultDetail.getRewardAmount();
            resultDetails.add(freeResultDetail);
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
                        .resultDetailDo(resultDetail)
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
