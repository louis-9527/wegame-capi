package com.wegame.capi.biz.domain.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
public class RewardResultDo implements Serializable {
    private static final long serialVersionUID = -2262432373454848L;
    /**
     * 中奖总金额.单位：分
     */
    private Long rewardTotalAmount;
    /**
     * 中奖线中奖列表
     */
    private List<RewardLineDo> rewardLineList;
    /**
     * wild总倍数
     */
    private BigDecimal wildTotalTimes;
    /**
     * 中奖类型  单奖励/多奖励
     */
    private Integer rewardType;
    /**
     * 中奖的所有元素的坐标集合
     */
    private Integer[][] rewardIndex;
    /**
     * [[]]
     */
    /**
     * 是否中了免费轮
     */
    private boolean isFreeSpin;
}
