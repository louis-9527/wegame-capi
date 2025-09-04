package com.wegame.capi.biz.domain.biz.template.malakas;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "malakas_game_record_list_template", createIndex = true)
public class MalakasGameRecordTemplateListEsDo implements Serializable {
    private static final long serialVersionUID = 1814235315L;

    @Id
    @Field(type = FieldType.Keyword)
    private Long betRecordId;

    private List<MalakasGameRecordTemplateEsDo> list;

}
