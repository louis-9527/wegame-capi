package com.wegame.capi.client.domain.request.query;

import com.wegame.base.domain.PageQueryRequest;
import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;


/**
 * 分页查询jackpot奖池请求参数
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "分页查询jackpot奖池请求参数", parent = PageQueryRequest.class)
public class PageQueryJackpotRewardPoolRequest extends PageQueryRequest implements Validate {
    private static final long serialVersionUID = -266823229820966912L;
    @ApiModelProperty(value = "主键ID", required = false)
    private String id;
    @ApiModelProperty(value = "类型 1：grand 2:major 3:minor 4:mini", required = false)
    private Integer type;
    @ApiModelProperty(value = "奖池金额.单位：厘， 可以为负", required = false)
    private String amount;
    @ApiModelProperty(value = "创建人", required = false)
    private String creator;
    @ApiModelProperty(value = "创建时间", required = false)
    private Long createTime;
    @ApiModelProperty(value = "更新人", required = false)
    private String updater;
    @ApiModelProperty(value = "更新时间", required = false)
    private Long updateTime;
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
        // AssertUtil.assertNotNull(type, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(amount, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(creator, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(createTime, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(updater, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(updateTime, ErrorCode.ERR_PARAM);
    }

    @Override
    public void validateBiz() {

    }
}

