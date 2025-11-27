package com.wegame.capi.biz.converter;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.operation.BetRecordTemplateDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.domain.request.query.PageQueryBetRecordRequest;
import com.wegame.capi.client.domain.vo.BetRecordVo;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.dao.repository.domain.business.BetRecordQuery;
import com.wegame.capi.dao.repository.domain.template.BetRecordTemplatePo;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * BetRecordTemplateConverter
 *
 * @author generate
 */
public class BetRecordTemplateConverter {

    public static BetRecordTemplateDo poToDo(BetRecordTemplatePo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        BetRecordTemplateDo betRecordDo = new BetRecordTemplateDo();
        betRecordDo.setId(po.getId());
        betRecordDo.setGameId(po.getGameId());
        betRecordDo.setBetType(po.getBetType());
        betRecordDo.setBetSubType(po.getBetSubType());
        betRecordDo.setMultiple(po.getMultiple());
        betRecordDo.setTemplateType(po.getTemplateType());
        betRecordDo.setHash(po.getHash());
        return betRecordDo;
    }

    public static BetRecordTemplatePo doToPo(BetRecordTemplateDo betRecordDo, OptEnum optEnum) {
        if (Objects.isNull(betRecordDo)) {
            return null;
        }
        BetRecordTemplatePo po = new BetRecordTemplatePo();
        po.setId(betRecordDo.getId());
        po.setGameId(betRecordDo.getGameId());
        po.setBetType(betRecordDo.getBetType());
        po.setBetSubType(betRecordDo.getBetSubType());
        po.setMultiple(betRecordDo.getMultiple());
        po.setTemplateType(betRecordDo.getTemplateType());
        po.setHash(betRecordDo.getHash());
        return po;
    }

    public static BetRecordVo doToVo(BetRecordDo betRecordDo) {
        if (Objects.isNull(betRecordDo)) {
            return null;
        }
        BetRecordVo vo = new BetRecordVo();
        vo.setId(Objects.nonNull(betRecordDo.getId()) ? String.valueOf(betRecordDo.getId()) : null);
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
        return vo;
    }

    public static List<BetRecordTemplateDo> poToDoList(List<BetRecordTemplatePo> poList) {
        return Optional.ofNullable(poList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BetRecordTemplateConverter::poToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<BetRecordTemplatePo> doToPoList(List<BetRecordTemplateDo> doList, OptEnum optEnum) {
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
                .map(record -> BetRecordTemplateConverter.doToVo(record))
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
        query.setStartCreateTime(request.getStartCreateTime());
        query.setEndCreateTime(request.getEndCreateTime());
        query.setOrderByClause(orderClause);
        query.setLimit(request.getPageSize());
        query.setLtLastId(Optional.ofNullable(request.getLastId()).map(Long::valueOf).orElse(null));
        query.setDirection(Sort.Direction.DESC);
        return query;
    }

    ///////////////  above auto generated ///////////////

}