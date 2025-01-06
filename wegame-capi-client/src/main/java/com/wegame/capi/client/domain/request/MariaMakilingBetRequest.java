package com.wegame.capi.client.domain.request;

import com.wegame.base.Validate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "MariaMakiling正常投注请求参数")
public class MariaMakilingBetRequest implements Validate, Serializable {
    @ApiModelProperty(value = "投注金额", required = true)
    private Long betAmount;
    @ApiModelProperty(value = "支付金额", required = true)
    private Long payAmount;
    @ApiModelProperty(value = "投注记录ID, 获得免费轮的投注记录的投注id，非必填", required = false)
    private String betRecordId;
    @ApiModelProperty(value = "免费轮ID, 非必填", required = false)
    private Integer freeSpinsId;
    @Override
    public void validateParam() {
        // todo
//        AssertUtil.assertNotNull(betAmount, ErrorCode.ERR_PARAM);
//        AssertUtil.assertTrue(betAmount > 0L, ErrorCode.ERR_PARAM);
    }
}
