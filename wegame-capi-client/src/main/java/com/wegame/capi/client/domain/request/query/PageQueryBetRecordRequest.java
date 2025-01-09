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
 * 分页查询投注记录请求参数
 *
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "分页查询投注记录请求参数", parent = PageQueryRequest.class)
public class PageQueryBetRecordRequest extends PageQueryRequest implements Validate {
    private static final long serialVersionUID = -8029880166975261696L;
    @ApiModelProperty(value = "投注记录id", required = false)
    private String id;
    @ApiModelProperty(value = "用户id", required = false)
    private String userId;
    @ApiModelProperty(value = "玩家id", required = false)
    private String playerId;
    @ApiModelProperty(value = "开始创建/投注时间", required = false)
    private Long startCreateTime;
    @ApiModelProperty(value = "结束创建/投注时间", required = false)
    private Long endCreateTime;
    @ApiModelProperty(value = "上一页最后一条数据的createTime", required = false)
    private Long lastCreateTime;
}

