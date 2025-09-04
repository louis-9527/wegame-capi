package com.wegame.capi.biz.domain.biz.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "malakas_game_record_template", createIndex = true)
public class MalakasGameRecordTemplateEsDo extends MalakasBetRecordTemplateEsDo {
    private static final long serialVersionUID = 181L;
}
