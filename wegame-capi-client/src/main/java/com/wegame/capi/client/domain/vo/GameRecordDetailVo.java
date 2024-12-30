package com.wegame.capi.client.domain.vo;

import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * game_record_detail：游戏记录明细
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("游戏记录明细")
@EqualsAndHashCode(callSuper = false)
public class GameRecordDetailVo implements Validate, Serializable {
    private static final long serialVersionUID = -856751800465119232L;
    @ApiModelProperty("主键ID")
    private String id;
    @ApiModelProperty("投注金额 单位：分")
    private String betAmount;
    @ApiModelProperty("派彩金额 单位：分")
    private String payOutAmount;
    @ApiModelProperty("游戏结果json")
    private String gameResult;
    @ApiModelProperty("中奖集合json")
    private String winResult;
    @ApiModelProperty("赔率")
    private BigDecimal odds;
    @ApiModelProperty("倍数")
    private Integer multiple;

    @Override
    public void validateParam() {
        AssertUtil.assertStrNotEmpty(id, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(betAmount, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(payOutAmount, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(gameResult, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(winResult, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(odds, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(multiple, ErrorCode.ERR_PARAM);
    }

    @Override
    public void validateBiz() {

    }

}

