package com.wegame.capi.client.domain.request.promotion;

import com.wegame.base.Validate;
import com.wegame.base.domain.PageQueryRequest;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "分页查询历史消息请求参数", parent = PageQueryRequest.class)
public class QueryHistoryMessageRequest
        extends PageQueryRequest
        implements Validate, Serializable {
    private static final long serialVersionUID = 746200490436329039L;

}
