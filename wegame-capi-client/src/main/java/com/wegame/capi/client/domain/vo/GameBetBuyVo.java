package com.wegame.capi.client.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("购买免费轮")
@EqualsAndHashCode(callSuper = false)
public class GameBetBuyVo extends GameBetNormalVo{
    private static final long serialVersionUID = 5796484386742295897L;
    @ApiModelProperty("免费轮Id")
    private List<Integer> freeSpinIds;
}
