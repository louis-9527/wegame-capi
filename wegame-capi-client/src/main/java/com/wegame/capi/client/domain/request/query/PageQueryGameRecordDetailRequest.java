package com.wegame.capi.client.domain.request.query;

import com.wegame.base.domain.PageQueryRequest;
import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * 分页查询游戏记录明细请求参数
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "分页查询游戏记录明细请求参数", parent = PageQueryRequest.class)
public class PageQueryGameRecordDetailRequest extends PageQueryRequest implements Validate {
    private static final long serialVersionUID = -1088867133973528576L;
    @ApiModelProperty(value = "主键ID", required = false)
    private String id;
    @ApiModelProperty(value = "投注金额 单位：分", required = false)
    private String betAmount;
    @ApiModelProperty(value = "派彩金额 单位：分", required = false)
    private String payOutAmount;
    @ApiModelProperty(value = "游戏结果json", required = false)
    private String gameResult;
    @ApiModelProperty(value = "中奖集合json", required = false)
    private String winResult;
    @ApiModelProperty(value = "赔率", required = false)
    private BigDecimal odds;
    @ApiModelProperty(value = "倍数", required = false)
    private Integer multiple;
    @ApiModelProperty(value = "开始创建时间", required = false)
    private Long startCreateTime;
    @ApiModelProperty(value = "结束创建时间", required = false)
    private Long endCreateTime;
    @ApiModelProperty(value = "开始更新时间", required = false)
    private Long startUpdateTime;
    @ApiModelProperty(value = "结束更新时间", required = false)
    private Long endUpdateTime;

    @Override
    public void validateParam() {
        // AssertUtil.assertStrNotEmpty(id, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(betAmount, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(payOutAmount, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(gameResult, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(winResult, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(odds, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(multiple, ErrorCode.ERR_PARAM);
    }

    @Override
    public void validateBiz() {

    }
}

