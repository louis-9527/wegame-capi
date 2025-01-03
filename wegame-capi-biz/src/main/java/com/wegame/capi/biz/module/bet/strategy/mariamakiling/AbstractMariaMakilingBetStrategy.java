package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.alibaba.fastjson.JSON;
import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.converter.user.GameRecordConverter;
import com.wegame.capi.biz.domain.biz.GameBetResultDetailDo;
import com.wegame.capi.biz.domain.biz.GamePlayDo;
import com.wegame.capi.biz.domain.biz.GamePlayResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardLineDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelRewardDo;
import com.wegame.capi.biz.module.bet.strategy.AbstractBetStrategy;
import com.wegame.capi.biz.module.game.Game;
import com.wegame.capi.biz.module.game.MariaMakilingGame;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.PrizeTypeEnum;
import com.wegame.capi.dao.mapper.domain.user.GameRecordDetailPo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractMariaMakilingBetStrategy<T, R> extends AbstractBetStrategy<T, R> {

    @Autowired
    MariaMakilingGame mariaMakilingGame;

    @Override
    public Game getGame() {
        return mariaMakilingGame;
    }

    // todo fix


    public GameBetResultDetailDo getResultDetail(String gameId, BetTypeEnum betType, BetSubTypeEnum betSubType, Long betAmount) {
        List<GameBetResultDetailDo> resultDetail = this.getResultDetail(gameId, betType, betSubType, betAmount, false);
        return resultDetail.get(0);
    }

    public List<GameBetResultDetailDo> getResultDetail(String gameId, BetTypeEnum betType, BetSubTypeEnum betSubType, Long betAmount, Boolean freeSpinsFlag) {
        GamePlayDo gamePlay = GamePlayDo.builder()
                .gameType(GameTypeEnum.valueOfCode(gameId))
                .betType(betType)
                .betSubType(betSubType)
                .betAmount(betAmount)
                .freeSpinsFlag(freeSpinsFlag)
                .build();
        GamePlayResultDo gamePlayResult = getGame().play(gamePlay);
        AssertUtil.assertNotNull(gamePlayResult, ErrorCode.ERR_INTER);
        // 滚轮中奖列表
        List<WheelRewardDo> wheelRewardList = gamePlayResult.getWheelRewardList();

        List<GameBetResultDetailDo> resultDetails = new ArrayList<>();
        wheelRewardList.forEach(rewardResultDo -> {
            RewardResultDo rewardResult = rewardResultDo.getRewardResult();
            GameBetResultDetailDo build = GameBetResultDetailDo.builder()
                    .id(rewardResultDo.getWheelIndex())
                    .gameResult(rewardResultDo.getWheel().getSymbolArr())
                    .betAmount(betAmount)
                    .build();
            if (rewardResult != null) {
                build.setRewardAmount(rewardResult.getRewardTotalAmount());
                build.setMultiple(rewardResult.getWildTotalTimes());
                build.setPrizeType(this.getRewardAmountType());
                build.setFreeSpinsFlag(rewardResult.isFreeSpin());
                build.setRewardResults(this.getRewardLineResults(rewardResult.getRewardLineList()));
            }
            resultDetails.add(build);
        });
        return resultDetails;
    }


    /**
     * 生成中奖线
     *
     * @param rewardLineList
     * @return
     */
    private List<GameBetResultDetailDo.RewardResult> getRewardLineResults(List<RewardLineDo> rewardLineList) {
        if (CollectionUtils.isEmpty(rewardLineList)) {
            return new ArrayList<>();
        }
        List<GameBetResultDetailDo.RewardResult> rewardResults = new ArrayList<>();
        rewardLineList.forEach(tt ->
                rewardResults.add(GameBetResultDetailDo.RewardResult.builder()
                        .symbol(tt.getSymbolDo())
                        .odds(tt.getOdds())
                        .multiple(tt.getRewardMultiple())
                        .rewardAmount(tt.getRewardAmount())
                        .rewardIndex(tt.getRewardSymbolIndexList())
                        .rewardSymbolList(tt.getRewardSymbolList())
                        .build()));
        return rewardResults;
    }


    private PrizeTypeEnum getRewardAmountType() {
        return PrizeTypeEnum.NONE_PRIZE;
    }


    public GameBetResultDetailDo queryBetFreeSpin(String gameId, Long recordId, Integer freeSpinId) {
        com.wegame.capi.biz.domain.user.BetRecordDo betRecordDo = betRecordService.get(recordId);
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);
        Object detail = betRecordDo.getDetail();
        if (Objects.isNull(detail)) {
            return null;
        }
        List<GameRecordDetailPo> recordDetailPos = JSON.parseArray(FastJsonUtils.toJSONString(detail), GameRecordDetailPo.class);
        GameRecordDetailPo recordDetail = new GameRecordDetailPo();
        for (GameRecordDetailPo detailPo : recordDetailPos) {
            if (Objects.equals(freeSpinId, detailPo.getId())) {
                recordDetail = detailPo;
                break;
            }
        }
        return GameRecordConverter.poToDetailDo(recordDetail);
    }



}
