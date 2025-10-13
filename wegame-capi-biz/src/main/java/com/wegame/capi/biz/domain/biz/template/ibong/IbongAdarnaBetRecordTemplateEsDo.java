package com.wegame.capi.biz.domain.biz.template.ibong;

import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(indexName = "ibongadarna_bet_record_template", createIndex = true)
public class IbongAdarnaBetRecordTemplateEsDo extends BetRecordTemplateEsDo {

    private static final long serialVersionUID = 18009876567L;


}
