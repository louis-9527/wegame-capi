package com.wegame.capi.client.domain.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Malakas正常投注请求参数")
public class MalakasBetRequest  implements BetRequestValidate, Serializable {
    private static final long serialVersionUID = 8816381026140374720L;

    @ApiModelProperty(value = "投注金额", required = true)
    private Long betAmount;
    @ApiModelProperty(value = "支付金额/购买金额", required = true)
    private Long payAmount;
    @ApiModelProperty(value = "投注记录ID, 获得免费轮的投注记录的投注id，非必填", required = false)
    private String betRecordId;
    @ApiModelProperty(value = "免费轮游戏记录ID, 非必填", required = false)
    private String gameRecordId;
}
