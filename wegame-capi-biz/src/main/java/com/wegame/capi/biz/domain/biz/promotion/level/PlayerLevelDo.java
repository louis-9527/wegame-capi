package com.wegame.capi.biz.domain.biz.promotion.level;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "用户等级")
public class PlayerLevelDo implements Serializable {

    private static final long serialVersionUID = 1804882586472683413L;

    @ApiModelProperty(value = "playerName", required = true)
    private String playerName;
    @ApiModelProperty(value = "playerLevel", required = true)
    private int playerLevel;
    @ApiModelProperty(value = "playerExperience", required = true)
    private BigDecimal playerExperience;
    @ApiModelProperty(value = "playerExperience", required = true)
    private String refId;
}
