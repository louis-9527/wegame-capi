package com.wegame.capi.dao.repo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "MalakasBetRecordTemplate")
public class MalakasBetRecordTemplatePo implements Serializable {
    private static final long serialVersionUID = 180L;

    @Id
    @Field(type = FieldType.Keyword)
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
