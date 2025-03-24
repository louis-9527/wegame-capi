package com.wegame.capi.dao.mapper.domain.operation;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OperationEventConfigRecordPo implements Serializable {
    private static final long serialVersionUID = -2044839391564867115L;
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
     * 序号
     */
    private Integer index;
    /**
     * 免费轮
     */
    private List<Long> gameRecordIds;
    /**
     * 滚轮总倍数
     */
    private BigDecimal totalMultiple;
    /**
     * 滚轮
     */
    private Object wheel;
}
