package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.poi.hpsf.Decimal;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ApiModel(description = "free spin 投注")
public class FreeSpinVo implements Serializable {
    private static final long serialVersionUID = 343465397087279138L;
    @ApiModelProperty("Player name")
    private String playerName;
    @ApiModelProperty("Unique id of the bet")
    private String betId;
    @ApiModelProperty("Player balance")
    private Decimal credit;
    @ApiModelProperty("Request id of the request")
    private String refId;
    @ApiModelProperty("The number of free spins remaining in the player's activity")
    private Integer remainingCount;
    @ApiModelProperty("Campaign id of the free spin")
    private String campaignId;
}
