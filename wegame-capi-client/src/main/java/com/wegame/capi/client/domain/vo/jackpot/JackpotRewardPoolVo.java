package com.wegame.capi.client.domain.vo.jackpot;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * jackpot_reward_pool：jackpot奖池
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("jackpot奖池")
@EqualsAndHashCode(callSuper = false)
public class JackpotRewardPoolVo implements Validate, Serializable {
    private static final long serialVersionUID = -4910489586462042112L;
    @ApiModelProperty("主键ID")
    private String id;
    @ApiModelProperty("类型 1：grand 2:major 3:minor 4:mini")
    private Integer type;
    @ApiModelProperty("奖池金额")
    private Long amount;
}

