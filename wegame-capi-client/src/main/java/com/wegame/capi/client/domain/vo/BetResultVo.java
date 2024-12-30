package com.wegame.capi.client.domain.vo;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * user：投注结果
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("投注结果")
@EqualsAndHashCode(callSuper = false)
public class BetResultVo implements Validate, Serializable {
    private static final long serialVersionUID = -7365176731574744064L;
    @ApiModelProperty("游戏Id")
    private Long gameId;
    @ApiModelProperty("投注类型")
    private Integer betType;
    @ApiModelProperty("投注结果")
    private Object betResult;

    @Data
    @NoArgsConstructor
//    @AllArgsConstructor
    @ApiModel(description = "Mysterious正常投注结果")
    public class MysteriousNormalBetResultVo implements Validate, Serializable {
        // todo
    }

    @Data
    @NoArgsConstructor
//    @AllArgsConstructor
    @ApiModel(description = "Mysterious非购买免费轮投注结果")
    public class MysteriousNonBuyFreeSpinsBetVo implements Validate, Serializable {
        // todo
    }

    @Data
    @NoArgsConstructor
//    @AllArgsConstructor
    @ApiModel(description = "Mysterious购买免费轮投注请求参数")
    public class MysteriousBuyFreeSpinsBetResultVo implements Validate, Serializable {
        // todo
    }
}

