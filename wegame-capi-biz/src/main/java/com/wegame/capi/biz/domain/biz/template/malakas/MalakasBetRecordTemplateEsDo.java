package com.wegame.capi.biz.domain.biz.template.malakas;

import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@NoArgsConstructor
//@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(indexName = "malakas_bet_record_template", createIndex = true)
public class MalakasBetRecordTemplateEsDo extends BetRecordTemplateEsDo {

    private static final long serialVersionUID = 180432143L;

    /**
     * bytes： List<T>的json格式后compress. T为具体游戏的模版
     */
    private byte[] bytes;
}
