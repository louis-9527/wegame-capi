package com.wegame.capi.biz.domain.biz.template.atlantis;

import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.common.constants.TemplateConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(indexName = TemplateConstants.ATLANTIS_BET_RECORD_TEMPLATE, createIndex = true)
public class AtlantisBetRecordTemplateEsDo extends BetRecordTemplateEsDo {

    private static final long serialVersionUID = 184567567L;


}
