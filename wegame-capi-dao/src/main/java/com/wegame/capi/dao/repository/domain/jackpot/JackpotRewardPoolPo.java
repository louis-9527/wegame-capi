package com.wegame.capi.dao.repository.domain.jackpot;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * jackpot_reward_pool：jackpot奖池
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JackpotRewardPoolPo implements Serializable {
    private static final long serialVersionUID = -1055354731135474688L;
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 类型 1：grand 2:major 3:minor 4:mini
     */
    private Integer type;
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
    private Date createTime;
    /**
     * 更新人
     */
    private String updater;
    /**
     * 更新时间
     */
    private Date updateTime;

   ///////////////  above auto generated ///////////////
}

