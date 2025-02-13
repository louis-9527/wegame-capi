package com.wegame.capi.biz.domain.biz.reward;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.common.enums.RewardSubTypeEnum;
import com.wegame.capi.common.enums.RewardTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 中奖结果do
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RewardResultDo<T extends SymbolDo> implements Serializable {
    private static final long serialVersionUID = -2262432373454848L;
    /**
     * 投注金额
     */
    private Long betAmount;
    /**
     * 中奖类型
     */
    private RewardTypeEnum rewardType;
    /**
     * 中奖子类型
     */
    private RewardSubTypeEnum rewardSubType;
    /**
     * 中奖总金额.单位：分
     */
    private Long totalRewardAmount;
    /**
     * 中奖总倍率
     */
    private BigDecimal totalMultiple;
    /**
     * 是否中了免费轮
     */
    private boolean hasRewardFreeWheel;
    /**
     * 中奖线列表
     */
    private List<RewardLineDetailDo<T>> rewardLineDetailList;

    @JsonIgnore
    public void print() {
        System.out.println("//////////////// 中奖结果 ////////////////");
        System.out.println("投注金额：" + betAmount);
        System.out.println("中奖总金额：" + totalRewardAmount);
        System.out.println("中奖总倍数：" + totalMultiple);
        System.out.println("中奖类型：" + rewardType.getDesc());
        System.out.println("中奖子类型：" + rewardSubType.getDesc());
        System.out.println("命中免费轮：" + (hasRewardFreeWheel ? "否" : "是"));
        System.out.println("中奖明细： " + rewardLineDetailList.size() + "条中奖线");
        Optional.ofNullable(rewardLineDetailList)
                .orElse(Collections.emptyList())
                .forEach(item -> {
                    item.print();
                });
    }
}
