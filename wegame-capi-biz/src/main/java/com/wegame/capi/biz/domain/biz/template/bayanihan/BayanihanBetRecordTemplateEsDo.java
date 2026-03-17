package com.wegame.capi.biz.domain.biz.template.bayanihan;

import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.common.constants.TemplateConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(indexName = TemplateConstants.BAYANIHAN_BET_RECORD_TEMPLATE_V1, createIndex = true)
public class BayanihanBetRecordTemplateEsDo extends BetRecordTemplateEsDo {

    private static final long serialVersionUID = 184567567L;


}
