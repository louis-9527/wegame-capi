package com.wegame.capi.client.domain.request.query;

import cn.hutool.core.util.NumberUtil;
import com.wegame.base.domain.PageQueryRequest;
import com.wegame.base.Validate;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;


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
    @ApiModelProperty(value = "开始创建/投注时间", required = false)
    private Long startCreateTime;
    @ApiModelProperty(value = "结束创建/投注时间", required = false)
    private Long endCreateTime;
    @ApiModelProperty(value = "上一页最后一条数据的Id", required = false)
    private String lastId;

    @Override
    public void validate() {
        if (StringUtils.isNotBlank(id)) {
            AssertUtil.assertTrue(NumberUtil.isLong(id), ErrorCode.ERR_PARAM);
        }
        if (StringUtils.isNotBlank(lastId)) {
            AssertUtil.assertTrue(NumberUtil.isLong(lastId), ErrorCode.ERR_PARAM);
        }
    }
}

