package com.wegame.capi.biz.domain.intervention;

import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlayerInterventionResultDo {
    /**
     * chainId
     */
    private String chainId;
    /**
     * chain Sort
     */
    private Integer chainSort;
    /**
     * 组件id
     */
    private String cmpId;
    /**
     * cmp Sort
     */
    private Integer cmpSort;
    /**
     * 干预模版name
     * eg:98rtp
     */
    private String rtpName;
    /**
     * 兜底干预模版name
     * eg:98rtp
     */
    private String backstopRtpName;
    /**
     * 倍率范围
     */
    private TemplateMultipleRange templateMultipleRange;
    /**
     * 模版结果
     */
    private BetRecordTemplateEsDo betRecordTemplateEsDo;

}
