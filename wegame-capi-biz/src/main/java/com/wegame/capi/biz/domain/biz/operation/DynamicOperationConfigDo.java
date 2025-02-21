package com.wegame.capi.biz.domain.biz.operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicOperationConfigDo {
    /**
     * 游戏ID
     */
    private String gameId;

    /**
     * 0:正常游戏
     * 1:购买免费游戏
     */
    private Integer configType;

    /**
     * 倍数
     */
    private Integer multiple;

    /**
     * 配置明细
     */
    private String detail;
}
