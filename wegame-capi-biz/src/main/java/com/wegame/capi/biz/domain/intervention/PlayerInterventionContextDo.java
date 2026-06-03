package com.wegame.capi.biz.domain.intervention;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.wegame.capi.common.enums.InterventionChainIdTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.*;

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
     * 多条链结果
     */
    private List<PlayerInterventionResultDo> chainInterventionResultList;

    public void addChainResult( PlayerInterventionResultDo result) {
        if (Objects.isNull(this.chainInterventionResultList)) {
            this.chainInterventionResultList= Lists.newArrayList();
        }
        this.chainInterventionResultList.add( result);
    }

    public PlayerInterventionResultDo getChainResult() {
        if (CollectionUtil.isEmpty(this.chainInterventionResultList)){
            return null;
        }
        this.chainInterventionResultList.sort(Comparator.comparing(PlayerInterventionResultDo::getCmpSort).reversed());
        return this.chainInterventionResultList.get(0);
    }

}
