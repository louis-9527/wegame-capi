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
     * 干预事件ID(igo返回)
     */
    private String eventId;
    /**
     * 干预事件ID(系统生成)
     */
    private Long operationRecordId;

    /**
     * 需要干预总金额
     */
    private Long bonus;

    /**
     * 干预事件明细ID(系统生成)
     */
    private Long operationRecordDetailId;

    private  BetRecordDo<R>  betRecordDo;

}
