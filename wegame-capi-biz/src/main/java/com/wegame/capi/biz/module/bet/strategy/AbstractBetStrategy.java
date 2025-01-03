package com.wegame.capi.biz.module.bet.strategy;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.IdGeneratorHelper;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.api.domain.res.singlewallet.BetWithPayoutRes;
import com.wegame.capi.biz.converter.user.GameRecordConverter;
import com.wegame.capi.biz.domain.user.BetRecordDo;
import com.wegame.capi.biz.domain.biz.GameBetResultDetailDo;
import com.wegame.capi.biz.domain.biz.GamePlayDo;
import com.wegame.capi.biz.domain.biz.GamePlayResultDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.reward.RewardLineDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelRewardDo;
import com.wegame.capi.biz.module.game.Game;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.GameStatusEnum;
import com.wegame.capi.common.enums.PrizeTypeEnum;
import com.wegame.capi.common.utils.GameAmountUtil;
import com.wegame.capi.dao.mapper.domain.user.BetRecordPo;
import com.wegame.capi.dao.mapper.domain.user.GameRecordDetailPo;
import org.apache.commons.collections4.CollectionUtils;
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
    IdGeneratorHelper idGeneratorHelper;
    // todo 删除 依赖
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    BetRecordService betRecordService;

    public abstract Game getGame();

    public BetRecordDo getBetRecord(BetDo<T> betReqDo, Long betAmount, Long payAmount, List<GameBetResultDetailDo> resultDetails) {
        //组装投注记录
        long id = idGeneratorHelper.nextId();
        return BetRecordDo.builder()
                .id(id)
                .pId("Pid")
                .channelId("channelId")
                .gameId(betReqDo.getGameId())
                .betSerialNo(String.valueOf(id))
                .betType(betReqDo.getBetType().getCode())
                .betSubType(betReqDo.getBetSubType().getCode())
                .betAmount(betAmount)
                .payOutAmount(payAmount)
                .status(GameStatusEnum.ING.getCode())
                .detail(resultDetails)
                .build();
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




    /**
     * 保存投注记录
     *
     * @return
     */
    public void saveBetRecordAndPayOut(BetRecordDo betRecordDo) {
        //组装中奖数据
        BetRecordPo gameRecordPo = GameRecordConverter.doToPo(betRecordDo);
        AssertUtil.assertNotNull(gameRecordPo, ErrorCode.ERR_INTER);
        gameRecordPo.setId(betRecordDo.getId());
        gameRecordPo.setBetSerialNo(String.valueOf(betRecordDo.getId()));
//        betRecordService.add() todo  betRecord所有读写操作 =》 betRecordService @tutu
        BetRecordPo insert = mongoTemplate.insert(gameRecordPo);
        //调用单钱包  注释规范 //后加空格
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
        mongoTemplate.updateFirst(query, update, BetRecordPo.class);
    }

    /**
     * 调用单钱包
     *
     * @param gameRecordPo
     * @return
     */

    private BetWithPayoutRes betWithPayout(BetRecordPo gameRecordPo) {
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
}
