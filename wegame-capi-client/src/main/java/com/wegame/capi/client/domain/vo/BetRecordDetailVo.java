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
import java.util.List;

/**
 * bet_record：游戏明细
 *
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("投注记录")
@EqualsAndHashCode(callSuper = false)
public class BetRecordDetailVo implements Validate, Serializable {
    private static final long serialVersionUID = -5835715678741184L;
    @ApiModelProperty("游戏明细列表")
    private List<BetRecordGameDetailVo> gameDetailVoList;
}

