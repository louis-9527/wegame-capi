package com.wegame.capi.dao.repository.domain.template;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

/**
 * 模板事件配置（模版）
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameRecordTemplateQuery extends GameRecordTemplatePo {
    private static final long serialVersionUID = -8759648659197707325L;
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
