package com.wegame.capi.biz.converter.jackpot;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolSummarizeDo;
import com.wegame.capi.client.domain.request.query.PageQueryJackpotRewardPoolRequest;
import com.wegame.capi.client.domain.vo.jackpot.JackpotRewardPoolSummarizeVo;
import com.wegame.capi.client.domain.vo.jackpot.JackpotRewardPoolVo;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JackpotRewardPoolConverter
 *
 * @author generate
 */
public class JackpotRewardPoolConverter {

    //    public static JackpotRewardPoolDo voToDo(JackpotRewardPoolVo vo) {
    //        if (Objects.isNull(vo)) {
    //            return null;
    //        }
    //        JackpotRewardPoolDo jackpotRewardPoolDo = new JackpotRewardPoolDo();
    //        jackpotRewardPoolDo.setType(JackpotRewardPoolTypeEnum.valueOfCode(vo.getType()));
    //        jackpotRewardPoolDo.setAmount(vo.getAmount());
    //        return jackpotRewardPoolDo;
    //    }

    public static JackpotRewardPoolDo poToDo(JackpotRewardPoolPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        JackpotRewardPoolDo jackpotRewardPoolDo = new JackpotRewardPoolDo();
        jackpotRewardPoolDo.setId(po.getId());
        jackpotRewardPoolDo.setType(JackpotRewardPoolTypeEnum.valueOfCode(po.getType()));
        jackpotRewardPoolDo.setAmount(po.getAmount());
        jackpotRewardPoolDo.setCreator(po.getCreator());
        jackpotRewardPoolDo.setCreateTime(po.getCreateTime());
        jackpotRewardPoolDo.setUpdater(po.getUpdater());
        jackpotRewardPoolDo.setUpdateTime(po.getUpdateTime());
        return jackpotRewardPoolDo;
    }

    public static JackpotRewardPoolPo doToPo(JackpotRewardPoolDo jackpotRewardPoolDo, OptEnum optEnum) {
        if (Objects.isNull(jackpotRewardPoolDo)) {
            return null;
        }
        Operator operator = OperatorContext.getOperator();
        AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
        JackpotRewardPoolPo po = new JackpotRewardPoolPo();
        po.setType(jackpotRewardPoolDo.getType().getCode());
        po.setAmount(jackpotRewardPoolDo.getAmount());
        if (OptEnum.ADD.equals(optEnum)) {
            po.setCreator(operator.getName());
            po.setCreateTime(DateUtil.getCurrentMilli());
            po.setUpdater(operator.getName());
            po.setUpdateTime(DateUtil.getCurrentMilli());
        } else {
            po.setUpdater(operator.getName());
            po.setUpdateTime(DateUtil.getCurrentMilli());
            po.setId(jackpotRewardPoolDo.getId());
        }
        return po;
    }

    public static JackpotRewardPoolVo doToVo(JackpotRewardPoolDo jackpotRewardPoolDo) {
        if (Objects.isNull(jackpotRewardPoolDo)) {
            return null;
        }
        JackpotRewardPoolVo vo = new JackpotRewardPoolVo();
        vo.setId(Objects.nonNull(jackpotRewardPoolDo.getId()) ? String.valueOf(jackpotRewardPoolDo.getId()) : null);
        vo.setType(jackpotRewardPoolDo.getType().getCode());
        vo.setAmount(Math.max(jackpotRewardPoolDo.getInitAmount(), jackpotRewardPoolDo.getAmount() + jackpotRewardPoolDo.getInitAmount()));
        return vo;
    }

    //    public static List<JackpotRewardPoolDo> voToDoList(List<JackpotRewardPoolVo> voList) {
    //        return Optional.ofNullable(voList)
    //                .orElse(Collections.emptyList())
    //                .stream()
    //                .map(JackpotRewardPoolConverter::voToDo)
    //                .filter(Objects::nonNull)
    //                .collect(Collectors.toList());
    //    }

    public static List<JackpotRewardPoolDo> poToDoList(List<JackpotRewardPoolPo> poList) {
        return Optional.ofNullable(poList)
                .orElse(Collections.emptyList())
                .stream()
                .map(JackpotRewardPoolConverter::poToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<JackpotRewardPoolPo> doToPoList(List<JackpotRewardPoolDo> doList, OptEnum optEnum) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> doToPo(item, optEnum))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<JackpotRewardPoolVo> doToVoList(List<JackpotRewardPoolDo> doList) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(JackpotRewardPoolConverter::doToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static JackpotRewardPoolQuery requestToQuery(PageQueryJackpotRewardPoolRequest request, String orderClause) {
        JackpotRewardPoolQuery query = new JackpotRewardPoolQuery();
        query.setId(StringUtils.isNotEmpty(request.getId()) ? Long.valueOf(request.getId()) : null);
        query.setType(request.getType());
        query.setAmount(StringUtils.isNotEmpty(request.getAmount()) ? Long.valueOf(request.getAmount()) : null);
        query.setCreator(request.getCreator());
        query.setCreateTime(Objects.nonNull(request.getCreateTime()) ? DateUtil.getCurrentMilli() : null);
        query.setUpdater(request.getUpdater());
        query.setUpdateTime(Objects.nonNull(request.getUpdateTime()) ? DateUtil.getCurrentMilli() : null);
        query.setStartCreateTime(request.getStartCreateTime());
        query.setEndCreateTime(Objects.nonNull(request.getEndCreateTime()) ? request.getStartCreateTime() : null);
        query.setStartUpdateTime(Objects.nonNull(request.getStartUpdateTime()) ? request.getStartCreateTime() : null);
        query.setEndUpdateTime(Objects.nonNull(request.getEndUpdateTime()) ? request.getEndUpdateTime() : null);
        query.setLimit(request.getPageSize());
        query.setOffset(request.getOffset());
        query.setOrderByClause(orderClause);
        return query;
    }

    /// ////////////  above auto generated ///////////////

    public static JackpotRewardPoolSummarizeVo doToVo(JackpotRewardPoolSummarizeDo summarize) {
        if (Objects.isNull(summarize)) {
            return null;
        }
        JackpotRewardPoolSummarizeVo vo = new JackpotRewardPoolSummarizeVo();
        if (Objects.nonNull(summarize.getGrand())) {
            vo.setGrand(doToVo(summarize.getGrand()));
        }
        if (Objects.nonNull(summarize.getMajor())) {
            vo.setGrand(doToVo(summarize.getMajor()));
        }
        if (Objects.nonNull(summarize.getMinor())) {
            vo.setGrand(doToVo(summarize.getMinor()));
        }
        if (Objects.nonNull(summarize.getMini())) {
            vo.setGrand(doToVo(summarize.getMini()));
        }
        vo.setTotalJackpotAmount(summarize.getTotalJackpotAmount());
        return vo;
    }

}