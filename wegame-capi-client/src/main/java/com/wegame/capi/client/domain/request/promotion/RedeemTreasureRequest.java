package com.wegame.capi.client.domain.request.promotion;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "领取宝箱请求模型")
public class RedeemTreasureRequest implements Validate, Serializable {
    private static final long serialVersionUID = -7035134333452916163L;
    @ApiModelProperty(value = "宝箱ID", required = true)
    private String treasureId;
    @ApiModelProperty(value = "宝箱等级", required = true)
    private Integer level;

    @Override
    public void validate() {
        AssertUtil.assertStrNotEmpty(treasureId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(level, ErrorCode.ERR_PARAM);
    }
}
