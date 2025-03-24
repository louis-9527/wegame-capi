package com.wegame.capi.client.domain.request.promotion;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "获取背包详情请求")
public class QueryRewardDetailRequest implements Validate, Serializable {
    private static final long serialVersionUID = -4002950733224509502L;

    @ApiModelProperty(value = "奖励配置id", required = true)
    private String rewardConfigId;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(rewardConfigId, ErrorCode.ERR_PARAM);
    }
}
