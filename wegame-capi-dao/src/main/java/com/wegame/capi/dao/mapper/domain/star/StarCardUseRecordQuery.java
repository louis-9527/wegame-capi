package com.wegame.capi.dao.mapper.domain.star;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StarCardUseRecordQuery extends StarCardUseRecordPo {
    private static final long serialVersionUID = -8901404698671384297L;
    /**
     * limit
     */
    private Integer limit;
    /**
     * offset
     */
    private Integer offset;

    /**
     * 小于createTime
     */
    private Long ltCreateTime;
    /**
     * 小于=createTime
     */
    private Long gteCreateTime;
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
