package com.wegame.capi.biz.converter;

import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.domain.request.query.PageQueryBetRecordRequest;
import com.wegame.capi.client.domain.vo.BetRecordVo;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.utils.TtlUtil;
import com.wegame.capi.dao.repository.domain.business.BetRecordPo;
import com.wegame.capi.dao.repository.domain.business.BetRecordQuery;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * BetRecordConverter
 *
 * @author generate
 */
public class BetRecordConverter {

    public static BetRecordDo poToDo(BetRecordPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        BetRecordDo betRecordDo = new BetRecordDo();
        betRecordDo.setId(po.getId());
        betRecordDo.setParentId(po.getParentId());
        betRecordDo.setPId(po.getPId());
        betRecordDo.setChannelId(po.getChannelId());
        betRecordDo.setGameId(po.getGameId());
        betRecordDo.setUserId(po.getUserId());
        betRecordDo.setPlayerId(po.getPlayerId());
        betRecordDo.setPlayerName(po.getPlayerName());
        betRecordDo.setBetPromotionType(po.getBetPromotionType());
        betRecordDo.setBetType(po.getBetType());
        betRecordDo.setBetSubType(po.getBetSubType());
        betRecordDo.setBetId(po.getBetId());
        betRecordDo.setBetSerialNo(po.getBetSerialNo());
        betRecordDo.setBetAmount(po.getBetAmount());
        betRecordDo.setPayAmount(po.getPayAmount());
        betRecordDo.setPayOutAmount(po.getPayOutAmount());
        betRecordDo.setReachMaxMultiple(po.getReachMaxMultiple());
        betRecordDo.setStatus(po.getStatus());
        if (Objects.nonNull(po.getExtendInfo())) {
            betRecordDo.setExtendInfo(BetRecordDo.ExtendInfo.builder()
                    .ieId(po.getExtendInfo().getIeId())
                    .rewardUrl(po.getExtendInfo().getRewardUrl())
                    .campaignId(po.getExtendInfo().getCampaignId())
                    .build());
        }
        if (Objects.nonNull(po.getJackpot())) {
            betRecordDo.setJackpot(BetRecordDo.Jackpot.builder()
                    .grandContributionAmount(po.getJackpot().getGrandContributionAmount())
                    .grandSupContributionAmount(po.getJackpot().getGrandSupContributionAmount())
                    .majorContributionAmount(po.getJackpot().getMajorContributionAmount())
                    .majorSupContributionAmount(po.getJackpot().getMajorSupContributionAmount())
                    .minorContributionAmount(po.getJackpot().getMinorContributionAmount())
                    .minorSupContributionAmount(po.getJackpot().getMinorSupContributionAmount())
                    .miniContributionAmount(po.getJackpot().getMiniContributionAmount())
                    .miniSupContributionAmount(po.getJackpot().getMiniSupContributionAmount())
                    .build());
        }
        betRecordDo.setCreator(po.getCreator());
        betRecordDo.setCreateTime(po.getCreateTime());
        betRecordDo.setUpdater(po.getUpdater());
        betRecordDo.setUpdateTime(po.getUpdateTime());
        betRecordDo.setFreeWheelHandleStatus(po.getFreeWheelHandleStatus());
        return betRecordDo;
    }

    public static BetRecordPo doToPo(BetRecordDo betRecordDo, OptEnum optEnum) {
        if (Objects.isNull(betRecordDo)) {
            return null;
        }
        BetRecordPo po = new BetRecordPo();
        po.setId(betRecordDo.getId());
        po.setParentId(betRecordDo.getParentId());
        po.setPId(betRecordDo.getPId());
        po.setChannelId(betRecordDo.getChannelId());
        po.setGameId(betRecordDo.getGameId());
        po.setUserId(betRecordDo.getUserId());
        po.setPlayerId(betRecordDo.getPlayerId());
        po.setPlayerName(betRecordDo.getPlayerName());
        po.setBetType(betRecordDo.getBetType());
        po.setBetSubType(betRecordDo.getBetSubType());
        po.setBetPromotionType(betRecordDo.getBetPromotionType());
        po.setBetId(betRecordDo.getBetId());
        po.setBetSerialNo(betRecordDo.getBetSerialNo());
        po.setBetAmount(betRecordDo.getBetAmount());
        po.setPayAmount(betRecordDo.getPayAmount());
        po.setPayOutAmount(betRecordDo.getPayOutAmount());
        po.setReachMaxMultiple(betRecordDo.getReachMaxMultiple());
        po.setStatus(betRecordDo.getStatus());
        po.setBetTime(betRecordDo.getBetTime());
        if (Objects.nonNull(betRecordDo.getExtendInfo())) {
            po.setExtendInfo(BetRecordPo.ExtendInfo.builder()
                    .ieId(betRecordDo.getExtendInfo().getIeId())
                    .rewardUrl(betRecordDo.getExtendInfo().getRewardUrl())
                    .campaignId(betRecordDo.getExtendInfo().getCampaignId())
                    .build());
        }
        if (Objects.nonNull(betRecordDo.getJackpot())) {
            po.setJackpot(BetRecordPo.Jackpot.builder()
                    .grandContributionAmount(betRecordDo.getJackpot().getGrandContributionAmount())
                    .grandSupContributionAmount(betRecordDo.getJackpot().getGrandSupContributionAmount())
                    .majorContributionAmount(betRecordDo.getJackpot().getMajorContributionAmount())
                    .majorSupContributionAmount(betRecordDo.getJackpot().getMajorSupContributionAmount())
                    .minorContributionAmount(betRecordDo.getJackpot().getMinorContributionAmount())
                    .minorSupContributionAmount(betRecordDo.getJackpot().getMinorSupContributionAmount())
                    .miniContributionAmount(betRecordDo.getJackpot().getMiniContributionAmount())
                    .miniSupContributionAmount(betRecordDo.getJackpot().getMiniSupContributionAmount())
                    .build());
        }
        if (OptEnum.ADD.equals(optEnum)) {
            po.setCreator(betRecordDo.getCreator());
            po.setCreateTime(betRecordDo.getCreateTime());
            po.setUpdater(betRecordDo.getUpdater());
            po.setUpdateTime(betRecordDo.getUpdateTime());
            po.setExpireAt(TtlUtil.calculateExpiredAt(betRecordDo.getCreateTime()));
        } else {
            po.setUpdater(betRecordDo.getUpdater());
            po.setUpdateTime(DateUtil.getCurrentMilli());
            po.setId(betRecordDo.getId());
        }
        po.setFreeWheelHandleStatus(betRecordDo.getFreeWheelHandleStatus());
        return po;
    }

    public static BetRecordVo doToVo(BetRecordDo betRecordDo) {
        if (Objects.isNull(betRecordDo)) {
            return null;
        }
        BetRecordVo vo = new BetRecordVo();
        vo.setId(Objects.nonNull(betRecordDo.getId()) ? String.valueOf(betRecordDo.getId()) : null);
        vo.setParentId(Objects.nonNull(betRecordDo.getParentId()) ? String.valueOf(betRecordDo.getParentId()) : null);
        vo.setUserId(Objects.nonNull(betRecordDo.getUserId()) ? String.valueOf(betRecordDo.getUserId()) : null);
        vo.setPlayerId(betRecordDo.getPlayerId());
        vo.setPlayerName(betRecordDo.getPlayerName());
        vo.setBetPromotionType(betRecordDo.getBetPromotionType());
        vo.setBetAmount(betRecordDo.getBetAmount());
        vo.setPayAmount(betRecordDo.getPayAmount());
        vo.setPayOutAmount(betRecordDo.getPayOutAmount());
        vo.setReachMaxMultiple(betRecordDo.getReachMaxMultiple());
        vo.setStatus(betRecordDo.getStatus());
        vo.setExtendInfo(Objects.nonNull(betRecordDo.getExtendInfo())
                ? BetRecordVo.ExtendInfo
                .builder()
                .rewardUrl(betRecordDo.getExtendInfo().getRewardUrl())
                .campaignId(betRecordDo.getExtendInfo().getCampaignId())
                .build()
                : null);
        vo.setCreateTime(betRecordDo.getCreateTime());
        vo.setFreeWheelHandleStatus(betRecordDo.getFreeWheelHandleStatus());
        return vo;
    }

    public static List<BetRecordDo> poToDoList(List<BetRecordPo> poList) {
        return Optional.ofNullable(poList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BetRecordConverter::poToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<BetRecordPo> doToPoList(List<BetRecordDo> doList, OptEnum optEnum) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> doToPo(item, optEnum))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<BetRecordVo> doToVoList(List<BetRecordDo> doList) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(record -> BetRecordConverter.doToVo(record))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static BetRecordQuery requestToQuery(PageQueryBetRecordRequest request, String orderClause) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        BetRecordQuery query = new BetRecordQuery();
        query.setId(Optional.ofNullable(request.getId()).map(Long::valueOf).orElse(null));
        query.setPlayerId(userInfo.getPlayerId());
        query.setChannelId(userInfo.getChannelId());
        query.setStatus(BetStatusEnum.SUCCESS.getCode());
        query.setStartCreateTime(Objects.nonNull(request.getStartCreateTime()) && request.getStartCreateTime() > 0 ? request.getStartCreateTime() : null);
        query.setEndCreateTime(Objects.nonNull(request.getEndCreateTime()) && request.getEndCreateTime() > 0 ? request.getEndCreateTime() : null);
        query.setOrderByClause(orderClause);
        query.setLimit(request.getPageSize());
        query.setLtLastId(Optional.ofNullable(request.getLastId()).map(Long::valueOf).orElse(null));
        query.setInPromotionTypeList(Lists.newArrayList(BetPromotionTypeEnum.NONE.getCode(),
                BetPromotionTypeEnum.OPERATION_EVENT.getCode(),
                BetPromotionTypeEnum.STAR_CARD.getCode()));
        query.setDirection(Sort.Direction.DESC);
        return query;
    }

    ///////////////  above auto generated ///////////////

}