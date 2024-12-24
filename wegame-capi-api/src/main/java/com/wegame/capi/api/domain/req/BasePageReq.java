package com.wegame.capi.api.domain.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 基础分页请求模型
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "", parent = BaseReq.class)
public class BasePageReq extends BaseReq {
    /**
     * 数据量，默认：10
     */
    private Integer limit = 10;

    /**
     * 页数，默认：1
     */
    private Integer currentPage = 1;
}
