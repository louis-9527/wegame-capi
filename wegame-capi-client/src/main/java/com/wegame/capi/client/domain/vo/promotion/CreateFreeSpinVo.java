package com.wegame.capi.client.domain.vo.promotion;

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
@EqualsAndHashCode
@ApiModel(description = "创建free spin记录")
public class CreateFreeSpinVo implements Serializable {
    private static final long serialVersionUID = -8976328882509491204L;
    @ApiModelProperty(value = "状态代码", required = true)
    private Integer result;
    @ApiModelProperty(value = "活动编号", required = true)
    private String eventId;
}
