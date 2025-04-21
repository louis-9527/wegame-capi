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
 * 分页查询游戏维护配置请求参数
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "分页查询游戏维护配置请求参数", parent = PageQueryRequest.class)
public class PageQueryGameMaintainConfigRequest extends PageQueryRequest implements Validate {
    private static final long serialVersionUID = -7948800316416408576L;
    @ApiModelProperty(value = "主键ID/配置id", required = false)
    private String id;
    @ApiModelProperty(value = "游戏ID", required = false)
    private String gameId;
    @ApiModelProperty(value = "维护开始时间", required = false)
    private Long maintainStartTime;
    @ApiModelProperty(value = "维护结束时间", required = false)
    private Long maintainEndTime;
    @ApiModelProperty(value = "启用状态 0:禁用 1:启用", required = false)
    private Integer enableStatus;
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
        // AssertUtil.assertStrNotEmpty(gameId, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(maintainStartTime, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(maintainEndTime, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(enableStatus, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(creator, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(createTime, ErrorCode.ERR_PARAM);
        // AssertUtil.assertStrNotEmpty(updater, ErrorCode.ERR_PARAM);
        // AssertUtil.assertNotNull(updateTime, ErrorCode.ERR_PARAM);
    }

    @Override
    public void validateBiz() {

    }
}

