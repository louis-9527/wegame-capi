package com.wegame.capi.dao.repository.domain.template;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * 模板事件配置（模版）
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BetRecordTemplateQuery extends BetRecordTemplatePo {
    private static final long serialVersionUID = -5951798927605339549L;
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
