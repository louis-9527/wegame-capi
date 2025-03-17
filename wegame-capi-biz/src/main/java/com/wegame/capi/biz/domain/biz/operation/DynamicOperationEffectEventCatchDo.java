package com.wegame.capi.biz.domain.biz.operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicOperationEffectEventCatchDo {

    /**
     * 事件id
     */
    private Long id;

    /**
     * 生效时间
     */
    private Long effectedTime;

    /**
     * 过期时间
     */
    private Long expiredTime;

    /**
     * 创建时间
     */
    private Long createTime;
}
