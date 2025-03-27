package com.wegame.capi.client.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"rowIndex", "columnIndex"}, callSuper = false)
@ApiModel("坐标")
public class IndexVo implements Serializable {
    private static final long serialVersionUID = -368260204369095L;
    @ApiModelProperty("行号")
    private int rowIndex;
    @ApiModelProperty("列号")
    private int columnIndex;
}
