package com.wegame.capi.api.domain.req.dynamicoperation;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DynamicOperationReq implements Serializable {
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
