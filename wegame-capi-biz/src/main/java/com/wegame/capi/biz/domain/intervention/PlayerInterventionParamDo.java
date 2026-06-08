package com.wegame.capi.biz.domain.intervention;

import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

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

    /**
     * 支付金额
     */
    private long payAmount;

    /**
     * 额外操作
     * key:cmp id
     * value:具体操作
     */
    private Map<String, Function> extOperator;

    public static PlayerInterventionParamDo buildDo(UserMetricInfoDo userMetricInfoDo, BetRecordTemplateEsDo betRecordTemplateEsDo, BetDo betDo) {
        return PlayerInterventionParamDo.builder()
                .userMetricInfoDo(userMetricInfoDo)
                .betRecordTemplateEsDo(betRecordTemplateEsDo)
                .betType(betDo.getBetType())
                .betAmount(betDo.getBetAmount())
                .payAmount(betDo.getPayAmount())
                .build();
    }

    public void addExtOperator(String cmpId, Function function) {
        if (Objects.isNull(this.extOperator)) {
            this.extOperator = new ConcurrentHashMap<>();
        }
        this.extOperator.put(cmpId, function);
    }

    public Function getExtOperatorFc(String cmpId) {
        if (Objects.isNull(this.extOperator)) {
            return null;
        }
        return this.extOperator.get(cmpId);
    }

}
