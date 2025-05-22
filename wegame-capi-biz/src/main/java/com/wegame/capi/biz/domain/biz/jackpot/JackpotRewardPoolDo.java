package com.wegame.capi.biz.domain.biz.jackpot;

import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * jackpot_reward_pool：jackpot奖池
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JackpotRewardPoolDo implements Serializable {
    private static final long serialVersionUID = -1614958476406033408L;
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 类型 1：grand 2:major 3:minor 4:mini
     */
    private JackpotRewardPoolTypeEnum type;
    /**
     * 奖池金额.单位：厘， 可以为负
     */
    private Long amount;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新人
     */
    private String updater;
    /**
     * 更新时间
     */
    private Long updateTime;

    ///////////////  above auto generated ///////////////

    /**
     * 初始金额
     */
    private Long initAmount;

    /**
     * 实际可派发的金额
     */
    private Long availableAmount;
}

