package com.wegame.capi.client.domain.vo.promotion.level;

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
public class PlayerLevelVo implements Serializable {
    private static final long serialVersionUID = -4885970038716839356L;

    @ApiModelProperty(value = "playerName", required = true)
    private String playerName;
    @ApiModelProperty(value = "playerLevel", required = true)
    private int playerLevel;
    @ApiModelProperty(value = "playerExperience", required = true)
    private BigDecimal playerExperience;
    @ApiModelProperty(value = "playerExperience", required = true)
    private String refId;
}
