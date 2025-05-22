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
    @ApiModelProperty("奖池金额.单位：厘， 可以为负")
    private String amount;
    //    @ApiModelProperty("创建人")
    //    private String creator;
    //    @ApiModelProperty("创建时间")
    //    private Long createTime;
    //    @ApiModelProperty("更新人")
    //    private String updater;
    //    @ApiModelProperty("更新时间")
    //    private Long updateTime;

    @ApiModelProperty("奖池初始金额.单位：厘")
    private String initAmount;

    @ApiModelProperty("奖池可派奖金额.单位：厘，max(amount, 保底金额)")
    private String availableAmount;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(id, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(type, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(amount, ErrorCode.ERR_PARAM);
        //        AssertUtil.assertStrNotEmpty(creator, ErrorCode.ERR_PARAM);
        //        AssertUtil.assertNotNull(createTime, ErrorCode.ERR_PARAM);
        //        AssertUtil.assertStrNotEmpty(updater, ErrorCode.ERR_PARAM);
        //        AssertUtil.assertNotNull(updateTime, ErrorCode.ERR_PARAM);
    }

    @Override
    public void validateBiz() {

    }

}

