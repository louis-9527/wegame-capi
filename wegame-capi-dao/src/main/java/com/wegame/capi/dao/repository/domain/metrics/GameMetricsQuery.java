package com.wegame.capi.dao.repository.domain.metrics;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * 大盘指标查询模型
 *
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GameMetricsQuery extends GameMetricsPo implements Serializable {
    private static final long serialVersionUID = -690987656787936L;
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
     * 排序语句
     */
    private Sort.Direction direction;
    /**
     * 开始创建时间
     */
    private Long startCreateTime;
    /**
     * 结束创建时间
     */
    private Long endCreateTime;
    /**
     * 开始更新时间
     */
    private Long startUpdateTime;
    /**
     * 结束更新时间
     */
    private Long endUpdateTime;

    ///////////////  above auto generated ///////////////

    /**
     * 小于最后一条的Id
     */
    private Long ltLastId;
    /**
     * 大于最后一条的Id
     */
    private Long gtLastId;
}

