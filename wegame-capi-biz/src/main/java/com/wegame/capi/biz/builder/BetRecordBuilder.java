package com.wegame.capi.biz.builder;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BetRecordBuilder {

    public static <T> BetRecordDo<T> build(Long betRecordId,
                                           BetDo betDo,
                                           Long payOutAmount,
                                           Boolean reachMaxMultiple,
                                           BetRecordDo.Jackpot jackpot,
                                           List<T> gameRecordList) {
        Operator operator = OperatorContext.getOperator();
        AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        AssertUtil.assertNotNull(betDo, ErrorCode.ERR_INTER);
        long currentMilli = DateUtil.getCurrentMilli();
        BetRecordDo.ExtendInfo extendInfo = null;
        BetPromotionTypeEnum betPromotionType = betDo.getBetPromotionType();
        AssertUtil.assertNotNull(betPromotionType, ErrorCode.ERR_INTER);
        if (Objects.equals(betPromotionType, BetPromotionTypeEnum.OPERATION_EVENT)) {
            extendInfo = BetRecordDo.ExtendInfo.builder()
                    .ieId(betDo.getGameOperationEvent().getOperationEvent().getEventId())
                    .build();
        } else if (Objects.equals(betPromotionType, BetPromotionTypeEnum.STAR_CARD)) {
            extendInfo = BetRecordDo.ExtendInfo.builder()
                    .rewardUrl(betDo.getRewardDetail().getDetails().getImgUrl())
                    .build();
        } else if (Objects.equals(betPromotionType, BetPromotionTypeEnum.FREE_SPIN)) {
            extendInfo = BetRecordDo.ExtendInfo.builder()
                    .campaignId(betDo.getCampaignId())
                    .build();
        }
        BetRecordDo.Inventory inventory = null;
        if (Objects.nonNull(betDo.getBetInventoryControl())) {
            inventory = BetRecordDo.Inventory.builder()
                    .inventoryType(Objects.nonNull(betDo.getBetInventoryControl().getInventoryType()) ?
                            betDo.getBetInventoryControl().getInventoryType().getCode() : null)
                    .inventoryPoolName(betDo.getBetInventoryControl().getInventoryPoolName())
                    .inventoryContributionAmount(betDo.getBetInventoryControl().getInventoryContributionAmount())
                    .build();
        }

        return BetRecordDo.<T>builder()
                .id(betRecordId)
                .parentId(betDo.getParentBetRecordId())
                .pId(userInfo.getPId())
                .channelId(userInfo.getChannelId())
                .playerId(userInfo.getPlayerId())
                .userId(Long.valueOf(userInfo.getId()))
                .playerName(userInfo.getPlayerName())
                .gameId(betDo.getGameId())
                .betSerialNo(String.valueOf(betRecordId))
                .betType(betDo.getBetType().getCode())
                .betSubType(betDo.getBetSubType().getCode())
                .betPromotionType(betPromotionType.getCode())
                .betAmount(betDo.getBetAmount())
                .payAmount(betDo.getPayAmount())
                .payOutAmount(payOutAmount)
                .reachMaxMultiple(reachMaxMultiple)
                .status(BetStatusEnum.ING.getCode())
                .jackpot(jackpot)
                .inventory(inventory)
                .extendInfo(extendInfo)
                .creator(operator.getName())
                .updater(operator.getName())
                .betTime(currentMilli)
                .createTime(currentMilli)
                .updateTime(currentMilli)
                .gameRecordList(gameRecordList)
                .build();
    }

}
