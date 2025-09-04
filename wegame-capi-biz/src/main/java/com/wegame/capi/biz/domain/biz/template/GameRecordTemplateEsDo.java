package com.wegame.capi.biz.domain.biz.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameRecordTemplateEsDo implements Serializable {

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
     * 滚轮
     */
    private Object wheel;
    /**
     * 免费轮
     */
    private List<Long> gameRecordIds;
}
