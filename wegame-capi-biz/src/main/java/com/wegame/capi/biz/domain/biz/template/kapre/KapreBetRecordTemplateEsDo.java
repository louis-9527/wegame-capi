package com.wegame.capi.biz.domain.biz.template.kapre;

import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.common.constants.TemplateConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(indexName = TemplateConstants.KAPRE_BET_RECORD_TEMPLATE, createIndex = true)
public class KapreBetRecordTemplateEsDo extends BetRecordTemplateEsDo {

    private static final long serialVersionUID = 1678243143L;

}
