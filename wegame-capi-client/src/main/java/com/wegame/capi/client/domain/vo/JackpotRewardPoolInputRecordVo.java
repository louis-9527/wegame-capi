package com.wegame.capi.client.domain.vo;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * jackpot_reward_pool_input_record：jackpot奖池流入记录
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("jackpot奖池流入记录")
@EqualsAndHashCode(callSuper = false)
public class JackpotRewardPoolInputRecordVo implements Validate, Serializable {
    private static final long serialVersionUID = -957834820118817792L;
    @ApiModelProperty("主键ID/投注记录ID")
    private String id;
    @ApiModelProperty("类型.10派彩后流入")
    private Integer type;
    @ApiModelProperty("grand金额")
    private String grandAmount;
    @ApiModelProperty("major金额")
    private String majorAmount;
    @ApiModelProperty("minor金额")
    private String minorAmount;
    @ApiModelProperty("mini金额")
    private String miniAmount;
    @ApiModelProperty("创建人")
    private String creator;
    @ApiModelProperty("创建时间")
    private Long createTime;
    @ApiModelProperty("更新人")
    private String updater;
    @ApiModelProperty("更新时间")
    private Long updateTime;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(id, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(type, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(grandAmount, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(majorAmount, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(minorAmount, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(miniAmount, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(creator, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(createTime, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(updater, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(updateTime, ErrorCode.ERR_PARAM);
    }

    @Override
    public void validateBiz() {

    }

}

