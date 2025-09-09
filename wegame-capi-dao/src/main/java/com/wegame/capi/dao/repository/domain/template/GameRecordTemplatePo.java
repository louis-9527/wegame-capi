package com.wegame.capi.dao.repository.domain.template;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameRecordTemplatePo implements Serializable {
    private static final long serialVersionUID = 7772981783085425990L;
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
     * 滚轮
     */
    private Object wheel;
    /**
     * 扩展属性，每个游戏根据实际需要定义值
     */
    private Object extInfo;
}
