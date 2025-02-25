package com.wegame.capi.biz.domain.biz.operation;

import com.wegame.capi.client.domain.vo.DynamicOperationStatusOfEventsRecordVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicOperationStatusOfEventsDo {
    /**
     * 事件ID
     */
    private String eventId;
    /**
     * 干预事件游戏金额
     */
    private Long executeBouns;
    /**
     * 执行时间
     */
    private Long executeTime;
    /**
     * 干预事件订单号
     */
    private List<String> betNos;
    /**
     * 干预事件订单信息
     */
    private List<DynamicOperationStatusOfEventsRecordDO> betRecords;
    /**
     * 干预事件状态码
     */
    private Integer eventStatus;
}
