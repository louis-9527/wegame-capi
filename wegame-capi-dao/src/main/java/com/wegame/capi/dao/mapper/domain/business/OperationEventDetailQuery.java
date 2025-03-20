package com.wegame.capi.dao.mapper.domain.business;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OperationEventDetailQuery implements Serializable {
    private static final long serialVersionUID = -3185563006843737525L;
    private Long id;
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
     * createTimeLt
     */
    private Long createTimeLt;
    /**
     * 上一页最后一条的Id
     */
    private Long lastId;
    /**
     * 排序语句
     */
    private String orderByClause;
    /**
     * 排序语句
     */
    private Sort.Direction direction;
}
