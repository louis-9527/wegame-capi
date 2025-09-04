package com.wegame.capi.biz.domain.biz.template.malakas;

import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "malakas_bet_record_template", createIndex = true)
public class MalakasBetRecordTemplateEsDo extends BetRecordTemplateEsDo {
    private static final long serialVersionUID = 180L;
}
