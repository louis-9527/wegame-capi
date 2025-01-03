package com.wegame.capi.client.domain.request;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "MariaMakiling非购买免费轮投注请求参数")
public class MariaMakilingNonBuyFreeSpinsBetRequest implements Validate, Serializable {
    @ApiModelProperty(value = "投注记录Id：获得免费轮投注记录的投注记录id", required = true)
    private String betRecordId;
    @ApiModelProperty(value = "免费轮ID,无则传空", required = true)
    private Integer freeSpinsId;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(betRecordId, ErrorCode.ERR_PARAM);
        if (Objects.nonNull(freeSpinsId)) {
            AssertUtil.assertNotNull(betRecordId, ErrorCode.ERR_PARAM);
        }
    }
}

