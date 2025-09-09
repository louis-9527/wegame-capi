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
@ApiModel(description = "领取任务请求模型")
public class RedeemDailyMissionRequest extends BaseRequest implements Validate, Serializable {
    private static final long serialVersionUID = 5858568029658968111L;
    @ApiModelProperty(value = "任务ID", required = true)
    private String dailyMissionId;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(dailyMissionId, ErrorCode.ERR_PARAM);
    }
}
