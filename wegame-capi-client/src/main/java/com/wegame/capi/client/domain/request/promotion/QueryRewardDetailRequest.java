package com.wegame.capi.client.domain.request.promotion;

import com.wegame.base.Validate;
import com.wegame.base.domain.BaseRequest;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "获取背包详情请求")
public class QueryRewardDetailRequest extends BaseRequest implements Validate, Serializable {
    private static final long serialVersionUID = -4002950733224509502L;

    @ApiModelProperty(value = "奖励配置id", required = true)
    private String rewardConfigId;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(rewardConfigId, ErrorCode.ERR_PARAM);
    }
}
