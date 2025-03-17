package com.wegame.capi.biz.domain.biz.operation;

import com.wegame.capi.biz.domain.biz.BetRecordDo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicOperationCheckResultDo<R> {

    /**
     * 是否命中干预事件
     */
    private Boolean eventResult;

    /**
     * 命中的干预事件
     */
    private DynamicOperationEventDo dynamicOperationEvent;

    /**
     * 干预事件明细ID(系统生成)
     */
    private Long operationRecordDetailId;

    /**
     * 干预金额
     */
    private Long eventAmount;

    private  BetRecordDo<R>  betRecordDo;

}
