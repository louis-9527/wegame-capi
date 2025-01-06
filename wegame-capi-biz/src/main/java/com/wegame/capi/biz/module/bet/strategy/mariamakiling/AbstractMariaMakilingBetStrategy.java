package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.Converter;
import com.wegame.capi.biz.domain.biz.MariaMakilingGameRecordDetailDo;
import com.wegame.capi.biz.domain.biz.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardLineDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelRewardDo;
import com.wegame.capi.biz.module.bet.strategy.AbstractBetStrategy;
import com.wegame.capi.biz.module.game.Game;
import com.wegame.capi.biz.module.game.MariaMakilingGame;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public abstract class AbstractMariaMakilingBetStrategy<T, R extends Converter> extends AbstractBetStrategy<T, R> {

    @Autowired
    MariaMakilingGame mariaMakilingGame;

    @Override
    public Game getGame() {
        return mariaMakilingGame;
    }






    //////////////////////  =============


    /**
     * 获取游戏结果
     *
     * @param betType    游戏类型
     * @param betSubType 游戏子类型
     * @param betAmount  投注金额
     * @return
     */
    public MariaMakilingGameRecordDetailDo getGameBetResult(BetTypeEnum betType, BetSubTypeEnum betSubType, Long betAmount) {
        return this.getGameBetResult(betType, betSubType, betAmount, Boolean.FALSE).get(0);
    }

    /**
     * 获取游戏结果
     *
     * @param betType       游戏类型
     * @param betSubType    游戏子类型
     * @param betAmount     投注金额
     * @return
     */
    public List<MariaMakilingGameRecordDetailDo> getGameBetResultList(BetTypeEnum betType, BetSubTypeEnum betSubType, Long betAmount) {
        return this.getGameBetResult(betType, betSubType, betAmount, Boolean.TRUE);
    }

    /**
     * 获取游戏结果
     *
     * @param betType       游戏类型
     * @param betSubType    游戏子类型
     * @param betAmount     投注金额
     * @param freeSpinsFlag 是否免费轮,如果是怎会获取多条记录
     * @return
     */
    private List<MariaMakilingGameRecordDetailDo> getGameBetResult(BetTypeEnum betType, BetSubTypeEnum betSubType, Long betAmount, Boolean freeSpinsFlag) {
        GamePlayDo gamePlay = GamePlayDo.builder()
                .gameType(this.mariaMakilingGame.getGameInfo().getGameType())
                .betType(betType)
                .betSubType(betSubType)
                .betAmount(betAmount)
                .freeSpinsFlag(freeSpinsFlag)
                .build();
        GamePlayResultDo gamePlayResult = getGame().play(gamePlay);
        AssertUtil.assertNotNull(gamePlayResult, ErrorCode.ERR_INTER);
        // 滚轮中奖列表
        List<WheelRewardDo> wheelRewardList = gamePlayResult.getWheelRewardList();
        List<MariaMakilingGameRecordDetailDo> resultDetails = new ArrayList<>();
        wheelRewardList.forEach(rewardResultDo -> {
            resultDetails.add(this.buildBetResult(rewardResultDo, betAmount));
        });
        return resultDetails;
    }


    /**
     * 生成中奖线
     *
     * @param rewardLineList
     * @return
     */
    private List<MariaMakilingGameRecordDetailDo.RewardLineResultDo> getRewardLineResults(List<RewardLineDo> rewardLineList) {
        if (CollectionUtils.isEmpty(rewardLineList)) {
            return new ArrayList<>();
        }
        List<MariaMakilingGameRecordDetailDo.RewardLineResultDo> rewardResults = new ArrayList<>();
        rewardLineList.forEach(tt ->
                rewardResults.add(MariaMakilingGameRecordDetailDo.RewardLineResultDo.builder()
                        .symbol(tt.getSymbol())
                        .odds(tt.getOdds())
                        .multiple(tt.getMultiple())
                        .rewardAmount(tt.getRewardAmount())
                        .rewardIndexList(tt.getSymbolIndexList())
                        .rewardSymbolList(tt.getSymbolList())
                        .build()));
        return rewardResults;
    }


    private MariaMakilingGameRecordDetailDo buildBetResult(WheelRewardDo rewardResultDo, Long betAmount) {
        MariaMakilingGameRecordDetailDo build = MariaMakilingGameRecordDetailDo.builder()
                .id(rewardResultDo.getWheelIndex())
                .gameResult(rewardResultDo.getWheel().getSymbolArr())
                .betAmount(betAmount)
                .build();
        RewardResultDo rewardResult = rewardResultDo.getRewardResult();
        if (rewardResult != null) {
            build.setRewardAmount(rewardResult.getRewardTotalAmount());
            build.setMultiple(rewardResult.getMultiple());
            build.setPrizeType(rewardResult.getRewardSubType());
            build.setFreeSpinsFlag(rewardResult.isFreeSpin());
            build.setRewardResults(this.getRewardLineResults(rewardResult.getRewardLineList()));
        }
        return build;
    }
}
