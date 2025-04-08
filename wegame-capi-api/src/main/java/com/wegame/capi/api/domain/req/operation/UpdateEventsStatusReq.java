package com.wegame.capi.api.domain.req.operation;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UpdateEventsStatusReq implements Serializable {
    private static final long serialVersionUID = -4392682699211806649L;
    /**
     * 干预事件ID
     */
    private String eventId;
    /**
     * 响应消息
     */
    private String requestTime;
    /**
     * 总干预的游戏金额
     */
    private BigDecimal executeBouns;
    /**
     * 开始执行时间
     */
    private String executeTime;
    /**
     * 干预事件订单号列表
     */
    private List<String> betNos;
    /**
     * 干预事件订单
     */
    private List<UpdateEventsBetRecordReq> betRecords;
    /**
     * 只接受 1 / 2 【干预完成 / 干预完成因为事件超时】
     */
    private Integer eventStatus;


    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class OperationReq implements Serializable {
        private static final long serialVersionUID = -142579944391963747L;
        /**
         * 商户代码
         */
        private String agentCode;
        /**
         * 加密后数据
         */
        private String message;
    }
}
