package com.wegame.capi.biz.domain.biz.template.mangkukulam;

import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.common.constants.TemplateConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(indexName = TemplateConstants.DIWATA_MAYARI_BET_RECORD_TEMPLATE, createIndex = true)
public class MangkukulamBetRecordTemplateEsDo extends BetRecordTemplateEsDo {

    @Field(type = FieldType.Keyword)
    private String tag;

    private static final long serialVersionUID = 180432143L;

}
