package com.wegame.capi.api.domain.res.promotion.level;

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
public class PlayerLevelRes implements Serializable {

    private static final long serialVersionUID = 1804882586472683413L;

    /**
     * playerName
     */
    private String playerName;
    /**
     * playerLevel
     */
    private int playerLevel;
    /**
     * playerExperience
     */
    private BigDecimal playerExperience;
    /**
     * refId
     */
    private String refId;
}
