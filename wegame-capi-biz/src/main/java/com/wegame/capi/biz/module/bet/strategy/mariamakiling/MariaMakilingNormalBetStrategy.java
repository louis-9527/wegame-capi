package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordBuilder;
import com.wegame.capi.biz.domain.biz.GamePlayDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingNormalBetDo;
import com.wegame.capi.biz.domain.biz.MariaMakilingGameRecordDetailDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingNormalBetResultDo;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayResultDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelRewardDo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("mariaMakilingNormalBetStrategy")
public class MariaMakilingNormalBetStrategy
        extends AbstractMariaMakilingBetStrategy<MariaMakilingNormalBetDo, MariaMakilingNormalBetResultDo> {
    @Override
    public BetResultDo<MariaMakilingNormalBetResultDo> bet(BetDo<MariaMakilingNormalBetDo> betDo) {
        List<WheelRewardDo> wheelRewardList = new ArrayList<>();
        GamePlayResultDo gamePlayResult = getGame().play(GamePlayDo.builder()
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(betDo.getBetDetail().getBetAmount())
                .freeSpinsFlag(false)
                .build());
        AssertUtil.assertNotNull(gamePlayResult, ErrorCode.ERR_INTER);
        if (gamePlayResult.hasReward()) {
            wheelRewardList.addAll(gamePlayResult.getWheelRewardList());
        }
        if (gamePlayResult.hasRewardFreeSpins()) {
            GamePlayResultDo freeSpinsGamePlayResult = getGame().play(GamePlayDo.builder()
                    .gameType(betDo.getGameType())
                    .betType(betDo.getBetType())
                    .betSubType(betDo.getBetSubType())
                    .betAmount(betDo.getBetDetail().getBetAmount())
                    .freeSpinsFlag(true)
                    .build());
            wheelRewardList.addAll(freeSpinsGamePlayResult.getWheelRewardList());
        }





//        MariaMakilingGameRecordDetailDo

        // 获取游戏结果
        MariaMakilingGameRecordDetailDo resultDetail = getGameBetResult(betDo.getBetType(), betDo.getBetSubType(), betDo.getBetDetail().getBetAmount());
        // 生成投注结果
        long betRecordId = idGeneratorHelper.nextId();
        BetRecordDo betRecord = BetRecordBuilder.build(betDo, betRecordId, resultDetail.getBetAmount(), resultDetail.getRewardAmount(), Lists.newArrayList(resultDetail));
        // 保存投注记录并派奖
        super.saveBetRecordAndPayOut(betRecord);
        // 组装返回结果
        return BetResultDo.<MariaMakilingNormalBetResultDo>builder()
                .betRecordId(betRecord.getId())
                .gameId(betDo.getGameId())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .resultDetail(MariaMakilingNormalBetResultDo.builder()
                        .resultDetail(resultDetail)
                        .build())
                .build();
    }
}
