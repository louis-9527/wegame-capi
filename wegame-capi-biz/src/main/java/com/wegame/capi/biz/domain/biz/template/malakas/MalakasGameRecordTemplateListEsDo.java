package com.wegame.capi.biz.domain.biz.template.malakas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "malakas_game_record_list_template", createIndex = true)
public class MalakasGameRecordTemplateListEsDo {
    private static final long serialVersionUID = 181L;

    private List<MalakasGameRecordTemplateEsDo> list;

}
