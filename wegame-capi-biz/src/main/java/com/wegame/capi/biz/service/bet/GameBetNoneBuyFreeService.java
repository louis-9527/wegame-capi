package com.wegame.capi.biz.service.bet;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.business.bet.*;
import com.wegame.capi.biz.service.GamePlayService;
import com.wegame.capi.dao.mapper.domain.user.GameRecordPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 非购买免费投注
 */
@Service
public class GameBetNoneBuyFreeService extends AbsGameBetService<GameBetNoneBuyReqDo, GameBetNoneBuyResultDo> {
    @Autowired
    private GamePlayService gamePlayService;


    @Override
    public GameBetResultDo<GameBetNoneBuyResultDo> bet(GameBetReqDo<GameBetNoneBuyReqDo> req) {
        Long recordId = req.getBetDetail().getRecordId();
        GameRecordPo gameRecord = super.mongoTemplate.findById(recordId, GameRecordPo.class);
        AssertUtil.assertNotNull(gameRecord, ErrorCode.ERR_PARAM);
        //获取轮盘
        List<Integer[][]> symBols = gamePlayService.getSysBolExcludeScatter(req.getGameId(), this.getRandomFreeSpins());
        //获取游戏结果
        List<GameBetResultDetailDo> resultDetails = new ArrayList<>();
        long rewardAmount = 0L;
        List<Integer> freeSpinIds = new ArrayList<>(symBols.size());
        for (int i = 0; i < symBols.size(); i++) {
            freeSpinIds.add(i);
            Integer[][] symBol = symBols.get(i);
            //获取游戏结果
            GameBetResultDetailDo resultDetail = super.getResultDetail(i, gameRecord.getBetAmount(), symBol);
            rewardAmount = rewardAmount + resultDetail.getRewardAmount();
            resultDetails.add(resultDetail);
        }
        //获取投注结果
        GameBetRecordDo betRecord = super.getBetRecord(req.getGameId(), 0L, rewardAmount, resultDetails);
        //保存投注记录并派奖

        super.saveBetRecordAndPayOut(betRecord);
        return GameBetResultDo.<GameBetNoneBuyResultDo>builder()
                .gameId(req.getGameId())
                .betType(req.getBetType())
                .betSubType(req.getBetSubType())
                .resultDetail(GameBetNoneBuyResultDo.builder().freeSpinIds(freeSpinIds).build())
                .build();
    }

    @Override
    public Class<GameBetNoneBuyReqDo> tClass() {
        return GameBetNoneBuyReqDo.class;
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
