package com.wegame.capi.dao.repository.domain.business;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

/**
 * 游戏记录查询模型
 *
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BetRecordQuery extends BetRecordPo implements Serializable {
    private static final long serialVersionUID = -69760526957607936L;
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

    /**
     * 投注子类型
     */
    private List<String> betSubTypes;
    /**
     * 指定查询的promotionType
     */
    private List<Integer> inPromotionTypeList;
}

