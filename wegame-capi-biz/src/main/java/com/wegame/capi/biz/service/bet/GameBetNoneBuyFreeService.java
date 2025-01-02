package com.wegame.capi.biz.service.bet;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.business.bet.*;
import com.wegame.capi.dao.mapper.domain.user.GameRecordPo;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 非购买免费投注
 */
@Service
public class GameBetNoneBuyFreeService extends AbsGameBetService<GameBetNoneBuyReqDo, GameBetNoneBuyResultDo> {


    @Override
    public GameBetResultDo<GameBetNoneBuyResultDo> bet(GameBetReqDo<GameBetNoneBuyReqDo> req) {
        Long recordId = req.getBetDetail().getRecordId();
        GameRecordPo gameRecord = super.mongoTemplate.findById(recordId, GameRecordPo.class);
        AssertUtil.assertNotNull(gameRecord, ErrorCode.ERR_PARAM);

        //获取游戏结果
        List<GameBetResultDetailDo> resultDetails = super.getResultDetail(req.getGameId(), req.getBetType(), req.getBetSubType(), 0L, Boolean.TRUE);
        long rewardAmount = 0L;
        List<Integer> freeSpinIds = new ArrayList<>(resultDetails.size());
        for (GameBetResultDetailDo resultDetail : resultDetails) {
            rewardAmount = rewardAmount + resultDetail.getRewardAmount();
            freeSpinIds.add(resultDetail.getId());
        }
        //获取投注结果
        GameBetRecordDo betRecord = super.getBetRecord(req, 0L, rewardAmount, resultDetails);
        //保存投注记录并派奖
        super.saveBetRecordAndPayOut(betRecord);
        return GameBetResultDo.<GameBetNoneBuyResultDo>builder()
                .gameId(req.getGameId())
                .betType(req.getBetType())
                .betSubType(req.getBetSubType())
                .resultDetail(GameBetNoneBuyResultDo.builder().freeSpinIds(freeSpinIds).build())
                .build();
    }

}
