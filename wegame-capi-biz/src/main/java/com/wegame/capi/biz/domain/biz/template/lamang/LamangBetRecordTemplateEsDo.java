package com.wegame.capi.biz.domain.biz.template.lamang;

import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(indexName = "lamang_bet_record_template", createIndex = true)
public class LamangBetRecordTemplateEsDo extends BetRecordTemplateEsDo {

    private static final long serialVersionUID = 180432143L;


}
