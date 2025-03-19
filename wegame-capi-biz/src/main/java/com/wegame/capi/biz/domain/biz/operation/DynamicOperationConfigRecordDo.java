package com.wegame.capi.biz.domain.biz.operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DynamicOperationConfigRecordDo implements Serializable {

    private static final long serialVersionUID = -5059259536926510662L;

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
     * 免费轮标识 false否 true是
     */
    private Boolean freeWheelFlag;
    /**
     * 滚轮
     */
    private Object wheel;

}
