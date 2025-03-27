package com.wegame.capi.dao.mapper.domain.operation;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

/**
 * 干预事件配置（模版）查询模型
 */
@Data
@ToString
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class OperationEventConfigQuery extends OperationEventConfigPo {
    /**
     * limit
     */
    private Integer limit;
    /**
     * offset
     */
    private Integer offset;
    /**
     * 排序语句
     */
    private String orderByClause;
    /**
     * 排序方向
     */
    private Sort.Direction direction;
}
