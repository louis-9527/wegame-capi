package com.wegame.capi.client.domain.vo;

import com.wegame.base.Validate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * bet_record：投注记录分页vo
 *
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("投注记录")
@EqualsAndHashCode(callSuper = false)
public class BetRecordGameDetailVo implements Validate, Serializable {
    private static final long serialVersionUID = -5835715678741184L;
    @ApiModelProperty("3*5的格子")
    private String[][] gameResult;
    @ApiModelProperty("wild倍数")
    private BigDecimal multiple;
    @ApiModelProperty("明细中奖金额")
    private Long rewardAmount;
}

