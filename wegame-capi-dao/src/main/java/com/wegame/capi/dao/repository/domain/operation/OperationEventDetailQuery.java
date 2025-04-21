package com.wegame.capi.dao.repository.domain.operation;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * 干预事件明细查询模型
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OperationEventDetailQuery implements Serializable {
    private static final long serialVersionUID = -3185563006843737525L;
    /**
     * 干预事件ID(系统)
     */
    private Long recordId;
    /**
     * 订单状态
     */
    private Integer betStatus;
    /**
     * limit
     */
    private Integer limit;
    /**
     * 小于createTime
     */
    private Long ltCreateTime;
    /**
     * 大于createTime
     */
    private Long gtCreateTime;
    /**
     * 大于最后一条的Id
     */
    private Long gtLastId;
    /**
     * 排序语句
     */
    private String orderByClause;
    /**
     * 排序方向
     */
    private Sort.Direction direction;
}
