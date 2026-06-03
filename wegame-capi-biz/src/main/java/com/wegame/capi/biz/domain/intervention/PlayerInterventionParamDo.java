package com.wegame.capi.biz.domain.intervention;

import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.client.enums.BetTypeEnum;
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
public class PlayerInterventionParamDo {

    /**
     * 用户采集信息
     */
    private UserMetricInfoDo userMetricInfoDo;
    /**
     * 模版结果
     */
    private BetRecordTemplateEsDo betRecordTemplateEsDo;
    /**
     * 投注类型
     */
    private BetTypeEnum betType;
    /**
     * 投注金额
     */
    private long betAmount;

    public static PlayerInterventionParamDo buildDo(UserMetricInfoDo userMetricInfoDo, BetRecordTemplateEsDo betRecordTemplateEsDo, BetTypeEnum betType, long betAmount){
        return PlayerInterventionParamDo.builder()
                .userMetricInfoDo(userMetricInfoDo)
                .betRecordTemplateEsDo(betRecordTemplateEsDo)
                .betType(betType)
                .betAmount(betAmount)
                .build();
    }

}
