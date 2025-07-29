package com.wegame.capi.dao.repository.domain.template;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * 干预事件配置（模版）
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BetRecordTemplateQuery implements Serializable {
    private static final long serialVersionUID = -1598498668016762207L;
    /**
     * 主键ID（bet_record_id）
     */
    @Id
    @Field("_id")
    private Long id;
    /**
     * 游戏ID
     */
    private String gameId;
    /**
     * 投注类型
     */
    private String betType;
    /**
     * 投注子类型
     */
    private String betSubType;
    /**
     * 倍数
     */
    private Double multiple;
    /**
     * 模版类型 common
     */
    private String templateType;
    /**
     * hash
     */
    private String hash;
}
