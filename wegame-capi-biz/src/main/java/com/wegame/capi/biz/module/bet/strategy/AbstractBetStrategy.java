package com.wegame.capi.biz.module.bet.strategy;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.BooleanEnum;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.IdGeneratorHelper;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.api.domain.res.singlewallet.BetWithPayoutRes;
import com.wegame.capi.biz.converter.user.GameRecordConverter;
import com.wegame.capi.biz.domain.biz.GameBetRecordDo;
import com.wegame.capi.biz.domain.biz.GameBetResultDetailDo;
import com.wegame.capi.biz.domain.biz.GamePlayDo;
import com.wegame.capi.biz.domain.biz.GamePlayResultDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.reward.RewardLineDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelRewardDo;
import com.wegame.capi.biz.module.game.Game;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.GameGrandPrizeTypeEnum;
import com.wegame.capi.common.enums.GameStatusEnum;
import com.wegame.capi.common.utils.GameAmountUtil;
import com.wegame.capi.dao.mapper.domain.user.GameRecordDetailPo;
import com.wegame.capi.dao.mapper.domain.user.GameRecordPo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 抽象投注策略<br>
 * 各游戏共性抽象
 */
@Component
public abstract class AbstractBetStrategy<T, R> implements BetStrategy<T, R> {

    @Autowired
    private IdGeneratorHelper idGeneratorHelper;
    @Autowired
    private SingleWalletApi singleWalletApi;
    @Autowired
    public MongoTemplate mongoTemplate;
    @Autowired
    private Game game;


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
        GamePlayResultDo gamePlayResult = game.play(gamePlay);
        //中奖结果
        List<WheelRewardDo> rewardResultList = gamePlayResult.getWheelRewardList();

        List<GameBetResultDetailDo> resultDetails = new ArrayList<>();
        rewardResultList.forEach(rewardResultDo -> {
            RewardResultDo rewardResult = rewardResultDo.getRewardResult();
            GameBetResultDetailDo build = GameBetResultDetailDo.builder()
                    .id(rewardResultDo.getWheelIndex())
                    .gameResult(rewardResultDo.getWheel().getSymbolArr())
                    .betAmount(betAmount)
                    .build();
            if (rewardResult != null) {
                build.setRewardAmount(rewardResult.getRewardTotalAmount());
                build.setMultiple(rewardResult.getWildTotalTimes());
                build.setRewardAmountType(this.getRewardAmountType());
                build.setFreeSpinsFlag(rewardResult.isFreeSpin() ? BooleanEnum.TRUE : BooleanEnum.FALSE);
                build.setRewardResults(this.getRewardLineResults(rewardResult.getRewardLineList()));
            }
            resultDetails.add(build);
        });

        //组装游戏记录
        return resultDetails;
    }


    public GameBetRecordDo getBetRecord(BetDo<T> betReqDo, Long betAmount, Long payAmount, List<GameBetResultDetailDo> resultDetails) {
        //组装投注记录
        long id = idGeneratorHelper.nextId();
        return GameBetRecordDo.builder()
                .id(id)
                .gameId(betReqDo.getGameId())
                .betSerialNo(String.valueOf(id))
                .betType(betReqDo.getBetType())
                .betSubType(betReqDo.getBetSubType())
                .betAmount(betAmount)
                .channelId("channelId")
                .pId("Pid")
                .payOutAmount(payAmount)
                .status(GameStatusEnum.ING.getCode())
                .resultDetails(resultDetails).build();
    }


    public GameBetResultDetailDo queryBetFreeSpin(String gameId, Long recordId, Integer freeSpinId) {
        GameRecordPo gameRecord = mongoTemplate.findById(recordId, GameRecordPo.class);
        AssertUtil.assertNotNull(gameRecord, ErrorCode.ERR_PARAM);
        String detail = gameRecord.getDetail();
        if (StringUtils.isEmpty(detail)) {
            return null;
        }
        List<GameRecordDetailPo> recordDetailPos = JSON.parseArray(detail, GameRecordDetailPo.class);
        GameRecordDetailPo recordDetail = new GameRecordDetailPo();
        for (GameRecordDetailPo detailPo : recordDetailPos) {
            if (Objects.equals(freeSpinId, detailPo.getId())) {
                recordDetail = detailPo;
                break;
            }
        }
        return GameRecordConverter.poToDetailDo(recordDetail);
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


    /**
     * 保存投注记录
     *
     * @return
     */
    public void saveBetRecordAndPayOut(GameBetRecordDo betRecordDo) {
        //组装中奖数据
        GameRecordPo gameRecordPo = GameRecordConverter.doToPo(betRecordDo);
        AssertUtil.assertNotNull(gameRecordPo, ErrorCode.ERR_INTER);
        gameRecordPo.setId(betRecordDo.getId());
        gameRecordPo.setBetSerialNo(String.valueOf(betRecordDo.getId()));
        GameRecordPo insert = mongoTemplate.insert(gameRecordPo);
        //调用单钱包
        BetWithPayoutRes betWithPayoutRes = this.betWithPayout(gameRecordPo);
        //判断返回结果
        String betId = betWithPayoutRes.getBetId();
        Query query = new Query(Criteria.where("id").is(insert.getId()));
        // 构建更新操作
        Update update = new Update();
        update.set("betId", betId);
        update.set("status", GameStatusEnum.SUCCESS.getCode());
        update.set("updateTime", new Date());
        //更新记录
        mongoTemplate.updateFirst(query, update, GameRecordPo.class);
    }

    /**
     * 调用单钱包
     *
     * @param gameRecordPo
     * @return
     */

    private BetWithPayoutRes betWithPayout(GameRecordPo gameRecordPo) {
        //调用单钱包
        BetWithPayoutReq payoutReq = BetWithPayoutReq.builder()
                .pId(gameRecordPo.getPId())
                .channelId(gameRecordPo.getChannelId())
                .playerName(gameRecordPo.getPlayerName())
                .refId(gameRecordPo.getBetSerialNo())
                .createTime(DateUtil.toUTC(new Date()))
                .betAmount(GameAmountUtil.toBigDecimal(gameRecordPo.getBetAmount()))
                .payoutAmount(GameAmountUtil.toBigDecimal(gameRecordPo.getPayOutAmount()))
                .betDetails(BetWithPayoutReq.BetDetails.builder()
                        .gameId(gameRecordPo.getGameId())
                        .roundNo(gameRecordPo.getBetSerialNo())
                        .wagersDate(DateUtil.getCurrentTimestamp())
                        .settlementDate(DateUtil.getCurrentTimestamp()).build())
                .build();
        BetWithPayoutRes betWithPayoutRes = new BetWithPayoutRes();//singleWalletApi.betWithPayout(payoutReq);
        betWithPayoutRes.setBetId("121");
        return betWithPayoutRes;
    }


    private GameGrandPrizeTypeEnum getRewardAmountType() {
        return GameGrandPrizeTypeEnum.NONE_PRIZE;
    }

}
