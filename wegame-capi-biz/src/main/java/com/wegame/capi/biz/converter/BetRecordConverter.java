package com.wegame.capi.biz.converter;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.MariaMakilingBetRecordDetailDo;
import com.wegame.capi.client.domain.request.query.PageQueryBetRecordRequest;
import com.wegame.capi.client.domain.vo.BetRecordVo;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.dao.mapper.domain.user.BetRecordPo;
import com.wegame.capi.dao.mapper.domain.user.BetRecordQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
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
        betRecordDo.setPId(po.getPId());
        betRecordDo.setChannelId(po.getChannelId());
        betRecordDo.setGameId(po.getGameId());
        betRecordDo.setUserId(po.getUserId());
        betRecordDo.setPlayerId(po.getPlayerId());
        betRecordDo.setPlayerName(po.getPlayerName());
        betRecordDo.setBetType(po.getBetType());
        betRecordDo.setBetSubType(po.getBetSubType());
        betRecordDo.setBetId(po.getBetId());
        betRecordDo.setBetSerialNo(po.getBetSerialNo());
        betRecordDo.setBetAmount(po.getBetAmount());
        betRecordDo.setPayOutAmount(po.getPayOutAmount());
        betRecordDo.setMarketFlag(po.getMarketFlag());
        betRecordDo.setStatus(po.getStatus());
        betRecordDo.setDetail(po.getDetail());
        betRecordDo.setCreator(po.getCreator());
        betRecordDo.setCreateTime(po.getCreateTime());
        betRecordDo.setUpdater(po.getUpdater());
        betRecordDo.setUpdateTime(po.getUpdateTime());
        return betRecordDo;
    }

    public static BetRecordPo doToPo(BetRecordDo betRecordDo, OptEnum optEnum) {
        if (Objects.isNull(betRecordDo)) {
            return null;
        }
        Operator operator = OperatorContext.getOperator();
        AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
        BetRecordPo po = new BetRecordPo();
        po.setId(betRecordDo.getId());
        po.setPId(betRecordDo.getPId());
        po.setChannelId(betRecordDo.getChannelId());
        po.setGameId(betRecordDo.getGameId());
        po.setUserId(betRecordDo.getUserId());
        po.setPlayerId(betRecordDo.getPlayerId());
        po.setPlayerName(betRecordDo.getPlayerName());
        po.setBetType(betRecordDo.getBetType());
        po.setBetSubType(betRecordDo.getBetSubType());
        po.setBetId(betRecordDo.getBetId());
        po.setBetSerialNo(betRecordDo.getBetSerialNo());
        po.setBetAmount(betRecordDo.getBetAmount());
        po.setPayAmount(betRecordDo.getPayAmount());
        po.setPayOutAmount(betRecordDo.getPayOutAmount());
        po.setMarketFlag(betRecordDo.getMarketFlag());
        po.setStatus(betRecordDo.getStatus());
        po.setDetail(betRecordDo.getDetail());
        if (OptEnum.ADD.equals(optEnum)) {
            long currentMilli = DateUtil.getCurrentMilli();
            po.setCreator(operator.getName());
            po.setCreateTime(DateUtil.getCurrentMilli());
            po.setUpdater(operator.getName());
            po.setUpdateTime(currentMilli);
        } else {
            po.setUpdater(operator.getName());
            po.setUpdateTime(DateUtil.getCurrentMilli());
            po.setId(betRecordDo.getId());
        }
        return po;
    }

    public static BetRecordVo doToVo(BetRecordDo betRecordDo, boolean includeDetail) {
        if (Objects.isNull(betRecordDo)) {
            return null;
        }
        BetRecordVo vo = new BetRecordVo();
        vo.setId(Objects.nonNull(betRecordDo.getId()) ? String.valueOf(betRecordDo.getId()) : null);
        vo.setPId(betRecordDo.getPId());
        vo.setChannelId(betRecordDo.getChannelId());
        vo.setUserId(Objects.nonNull(betRecordDo.getUserId()) ? String.valueOf(betRecordDo.getUserId()) : null);
        vo.setPlayerName(betRecordDo.getPlayerName());
        vo.setBetType(betRecordDo.getBetType());
        vo.setBetSubType(betRecordDo.getBetSubType());
        vo.setBetId(betRecordDo.getBetId());
        vo.setBetSerialNo(betRecordDo.getBetSerialNo());
        vo.setBetAmount(Objects.nonNull(betRecordDo.getBetAmount()) ? String.valueOf(betRecordDo.getBetAmount()) : null);
        vo.setPayOutAmount(Objects.nonNull(betRecordDo.getPayOutAmount()) ? String.valueOf(betRecordDo.getPayOutAmount()) : null);
        vo.setStatus(betRecordDo.getStatus());
        vo.setCreateTime(Objects.nonNull(betRecordDo.getCreateTime()) ? betRecordDo.getCreateTime() : null);
        if (includeDetail) {
            vo.setDetail(BetRecordDetailConverter.doToVo((MariaMakilingBetRecordDetailDo) betRecordDo.getDetail()));
        }
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
                .map(record -> BetRecordConverter.doToVo(record, true))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static BetRecordQuery requestToQuery(PageQueryBetRecordRequest request, String orderClause) {
        BetRecordQuery query = new BetRecordQuery();
        query.setUserId(StringUtils.isNotEmpty(request.getUserId()) ? Long.valueOf(request.getUserId()) : null);
        query.setStartCreateTime(Objects.nonNull(request.getStartCreateTime()) ? new Date(request.getStartCreateTime()) : null);
        query.setEndCreateTime(Objects.nonNull(request.getEndCreateTime()) ? new Date(request.getEndCreateTime()) : null);
        query.setStartUpdateTime(Objects.nonNull(request.getStartUpdateTime()) ? new Date(request.getStartUpdateTime()) : null);
        query.setEndUpdateTime(Objects.nonNull(request.getEndUpdateTime()) ? new Date(request.getEndUpdateTime()) : null);
        query.setOrderByClause(orderClause);
        return query;
    }

    ///////////////  above auto generated ///////////////

}