package com.wegame.capi.dao.mapper.domain.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DynamicOperationDetailQuery implements Serializable {
    private static final long serialVersionUID = -3185563006843737525L;
    private Long id;
    /**
     * 干预事件ID(系统)
     */
    private Long operationId;
    /**
     * 订单状态
     */
    private Integer betStatus;

    private Integer limit;

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
