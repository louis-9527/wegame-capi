package com.wegame.capi.dao.mapper.domain.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.mapping.Field;

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
@EqualsAndHashCode(callSuper = false)
public class GameRecordQuery implements Serializable {
    private static final long serialVersionUID = -6976052604957607936L;

    @Id
    @Field("_id")
    private Long id;
    /**
     * parentId
     */
    private Long parentId;
    /**
     * batchId
     */
    private Long batchId;
    /**
     * betRecordId
     */
    private Long betRecordId;
    /**
     * 玩家id
     */
    private String playerId;
    /**
     * 玩家名称
     */
    private String playerName;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新人
     */
    private String updater;
    /**
     * 更新时间
     */
    private Long updateTime;

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

    ///////////////  above auto generated ///////////////

    /**
     * 缓存key
     */
    private String cacheKey;
}

