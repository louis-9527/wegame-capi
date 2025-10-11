package com.wegame.capi.biz.converter.metrics;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.biz.domain.metrics.UserPlayMetricsDo;
import com.wegame.capi.dao.repository.domain.metrics.UserPlayMetricsPo;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpecialRuleConverter {

    public static UserPlayMetricsDo.SpecialRuleDo poToDo(UserPlayMetricsPo.SpecialRulePo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        UserPlayMetricsDo.SpecialRuleDo specialRuleDo = new UserPlayMetricsDo.SpecialRuleDo();
        specialRuleDo.setRuleId(po.getRuleId());
        specialRuleDo.setRuleName(po.getRuleName());
        specialRuleDo.setContinuousNoCount(po.getContinuousNoCount());
        specialRuleDo.setExtendInfo(po.getExtendInfo());
        return specialRuleDo;
    }

    public static List<UserPlayMetricsDo.SpecialRuleDo> poToDoList(List<UserPlayMetricsPo.SpecialRulePo> poList) {
        if (CollectionUtil.isEmpty(poList)) {
            return null;
        }

        return poList.stream()
                .map(SpecialRuleConverter::poToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static UserPlayMetricsPo.SpecialRulePo doToPo(UserPlayMetricsDo.SpecialRuleDo aDo) {
        if (Objects.isNull(aDo)) {
            return null;
        }
        UserPlayMetricsPo.SpecialRulePo po = new UserPlayMetricsPo.SpecialRulePo();
        po.setRuleId(aDo.getRuleId());
        po.setRuleName(aDo.getRuleName());
        po.setContinuousNoCount(aDo.getContinuousNoCount());
        po.setExtendInfo(aDo.getExtendInfo());
        return po;
    }

    public static List<UserPlayMetricsPo.SpecialRulePo> doToPoList(List<UserPlayMetricsDo.SpecialRuleDo> doList) {
        if (CollectionUtil.isEmpty(doList)) {
            return null;
        }

        return doList.stream()
                .map(SpecialRuleConverter::doToPo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


}
