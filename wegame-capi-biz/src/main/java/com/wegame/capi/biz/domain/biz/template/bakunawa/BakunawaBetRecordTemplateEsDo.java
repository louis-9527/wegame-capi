package com.wegame.capi.biz.domain.biz.template.bakunawa;

import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.common.constants.TemplateConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(indexName = TemplateConstants.BAKUNAWA_BET_RECORD_TEMPLATE, createIndex = true)
public class BakunawaBetRecordTemplateEsDo extends BetRecordTemplateEsDo {

    private static final long serialVersionUID = 180432143L;


}
