package com.wegame.capi.biz.domain.biz.template.malakas;

import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@ToString
@NoArgsConstructor
//@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(indexName = "malakas_bet_record_template", createIndex = true)
public class MalakasBetRecordTemplateEsDo extends BetRecordTemplateEsDo {

    private static final long serialVersionUID = 180L;

}
