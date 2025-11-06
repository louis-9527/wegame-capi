package com.wegame.capi.biz.domain.biz.template.mariamakiling;

import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.common.constants.TemplateConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(indexName = TemplateConstants.MARIAMAKILING_BET_RECORD_TEMPLATE, createIndex = true)
public class MariaMakilingBetRecordTemplateEsDo extends BetRecordTemplateEsDo {
    private static final long serialVersionUID = 4863641109910867487L;
}
