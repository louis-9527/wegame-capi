package com.wegame.capi.biz.domain.intervention;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 玩家干预上下文,链中结果存放
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlayerInterventionContextDo implements Serializable {
    private static final long serialVersionUID = -1567885600L;
    /**
     * 前一个组件结果
     */
    private PlayerInterventionResultDo preCmpResult;
    /**
     * 组件结果
     */
    private PlayerInterventionResultDo currentCmpResultPoint;
    /**
     * 多条链结果
     * key:cmpId
     * value:result
     */
    private Map<String, PlayerInterventionResultDo> chainInterventionResultMap;

    /**
     * 干预链
     */
    private List<InterventionTraceInfo> traceInfoList;

    public void addInterventionTraceInfo(InterventionTraceInfo traceInfo) {
        if (Objects.isNull(traceInfo)) {
            return;
        }
        if (CollectionUtil.isEmpty(this.traceInfoList)) {
            this.traceInfoList = Lists.newArrayList();
        }
        this.traceInfoList.add(traceInfo);

    }


    public void addChainResult(PlayerInterventionResultDo result) {
        if (CollectionUtil.isEmpty(this.chainInterventionResultMap)) {
            this.chainInterventionResultMap = new HashMap<>();
        }
        this.chainInterventionResultMap.put(result.getCmpId(), result);
    }

    public PlayerInterventionResultDo getChainResult(String cmpId) {
        if (CollectionUtil.isEmpty(this.chainInterventionResultMap)) {
            return null;
        }
        return this.chainInterventionResultMap.get(cmpId);
    }

    public PlayerInterventionResultDo getChainResult() {
        if (CollectionUtil.isEmpty(this.chainInterventionResultMap)) {
            return null;
        }
        List<PlayerInterventionResultDo> list = this.chainInterventionResultMap.values().stream()
                .sorted(Comparator.comparing(PlayerInterventionResultDo::getCmpSort).reversed()).collect(Collectors.toList());
        return list.get(0);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class InterventionTraceInfo {
        public InterventionTraceInfo(String chainId, String cmpId, String message) {
            this.chainId = chainId;
            this.cmpId = cmpId;
            this.message = message;
        }
        private String chainId;
        private String cmpId;
        private String message;
    }

}
