package com.wegame.capi.biz.domain.biz.template.mangkukulam;

import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MangkukulamBetRecordTemplateEsDo extends BetRecordTemplateEsDo {

    @Field(type = FieldType.Keyword)
    private String tag;

    private static final long serialVersionUID = 180432143L;

}
