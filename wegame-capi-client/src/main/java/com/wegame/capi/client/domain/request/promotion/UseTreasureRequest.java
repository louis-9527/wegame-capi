package com.wegame.capi.client.domain.request.promotion;

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
@ApiModel(description = "使用宝箱请求")
public class UseTreasureRequest  implements Validate, Serializable {
    private static final long serialVersionUID = -7035134333452916163L;
    @ApiModelProperty(value = "宝箱ID", required = true)
    private String id;
    @ApiModelProperty(value = "宝箱等级", required = true)
    private String level;
}
