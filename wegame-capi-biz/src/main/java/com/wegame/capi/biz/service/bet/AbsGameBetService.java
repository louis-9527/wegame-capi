package com.wegame.capi.biz.service.bet;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.IdGeneratorHelper;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.api.domain.res.singlewallet.BetWithPayoutRes;
import com.wegame.capi.biz.converter.user.GameRecordConverter;
import com.wegame.capi.biz.domain.business.bet.GameBetRecordDo;
import com.wegame.capi.biz.domain.business.bet.GameBetReqDo;
import com.wegame.capi.biz.domain.business.bet.GameBetResultDetailDo;
import com.wegame.capi.biz.domain.business.bet.GameBetResultDo;
import com.wegame.capi.biz.domain.game.RewardResultDo;
import com.wegame.capi.biz.domain.game.RewardRuleLineDo;
import com.wegame.capi.common.enums.GameBetSubTypeEnum;
import com.wegame.capi.common.enums.GameBetTypeEnum;
import com.wegame.capi.common.enums.GameRewardAmountTypeEnum;
import com.wegame.capi.common.enums.GameStatusEnum;
import com.wegame.capi.common.utils.GameAmountUtil;
import com.wegame.capi.dao.mapper.domain.user.GameRecordPo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 投注Service
 */
@Service
public abstract class AbsGameBetService<T, R> {
    @Autowired
    private IdGeneratorHelper idGeneratorHelper;
    @Autowired
    private SingleWalletApi singleWalletApi;
    @Autowired
    MongoTemplate mongoTemplate;

    public abstract GameBetResultDo<R> bet(GameBetReqDo<T> t);

    public abstract Class<T> tClass();


    /**
     * 生成中奖线
     *
     * @param rewardLineList
     * @return
     */
    private List<GameBetResultDetailDo.RewardResult> getRewardResults(List<RewardRuleLineDo> rewardLineList) {
        if (CollectionUtils.isEmpty(rewardLineList)) {
            return new ArrayList<>();
        }
        List<GameBetResultDetailDo.RewardResult> rewardResults = new ArrayList<>();
        rewardLineList.forEach(tt ->
                rewardResults.add(GameBetResultDetailDo.RewardResult.builder()
                        .symbolId(0)//todo
                        .odds(BigDecimal.ZERO)
                       // .rewardSymbolIdList(tt.getRewardSymbolIdList())
                        .rewardAmount(100L)
                        .build()));

        return rewardResults;
    }

    private GameRewardAmountTypeEnum getRewardAmountType() {
        return GameRewardAmountTypeEnum.NONE_PRIZE;
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

    public GameBetResultDetailDo getResultDetail(Integer id, Long betAmount, Integer[][] symBol) {
        RewardResultDo rewardResult = null;//rewardStrategy.handle(null, null, null,null,null);
        //组装游戏记录
        return GameBetResultDetailDo.builder()

                .betAmount(0L)
                .rewardAmount(100L)
                .gameResult(symBol)
                .multiple(BigDecimal.ONE)
                //.rewardIndex(new Integer[][]{})
                .rewardAmountType(this.getRewardAmountType())
                .rewardResults(new ArrayList<>())
                .build();
    }

    public GameBetRecordDo getBetRecord(String gameId, Long betAmount, Long payAmount, List<GameBetResultDetailDo> resultDetails) {
        //组装投注记录
        long id = idGeneratorHelper.nextId();
        return GameBetRecordDo.builder()
                .id(id)
                .gameId(gameId)
                .betSerialNo(String.valueOf(id))
                .betType(GameBetTypeEnum.NORMAL)
                .betSubType(GameBetSubTypeEnum.NORMAL)
                .betAmount(betAmount)
                .channelId("channelId")
                .pId("Pid")
                .payOutAmount(payAmount)
                .status(GameStatusEnum.ING.getCode())
                .resultDetails(resultDetails).build();
    }

}
