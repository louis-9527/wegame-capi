package com.wegame.capi.client.domain.vo.bakunawa;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Bakunawa元素vo")
@EqualsAndHashCode(callSuper = false)
public class BakunawaSymbolVo implements Serializable {
    private static final long serialVersionUID = 648778735380369L;
    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("分裂倍数")
    private Integer breakTimes;
}
