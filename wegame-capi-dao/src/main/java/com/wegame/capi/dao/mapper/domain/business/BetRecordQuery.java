package com.wegame.capi.dao.mapper.domain.business;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 游戏记录查询模型
 *
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BetRecordQuery extends BetRecordPo implements Serializable {
    private static final long serialVersionUID = -6976052604957607936L;
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
     * 上一夜最后一条的Id
     */
    private Long lastId;
}

