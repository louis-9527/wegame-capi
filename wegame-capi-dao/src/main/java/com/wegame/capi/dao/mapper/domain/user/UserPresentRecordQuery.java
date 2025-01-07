package com.wegame.capi.dao.mapper.domain.user;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户赠送记录查询模型
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPresentRecordQuery extends UserPresentRecordPo implements Serializable {
    private static final long serialVersionUID = -527548033526044672L;
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
    private Date startCreateTime;
    /**
     * 结束创建时间
     */
    private Date endCreateTime;
    /**
     * 开始更新时间
     */
    private Date startUpdateTime;
    /**
     * 结束更新时间
     */
    private Date endUpdateTime;

    ///////////////  above auto generated ///////////////

}

