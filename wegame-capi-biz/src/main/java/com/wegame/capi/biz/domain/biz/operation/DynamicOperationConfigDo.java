package com.wegame.capi.biz.domain.biz.operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicOperationConfigDo {
    private String id;
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
    private Double multiple;

    /**
     * 配置明细
     */
    private List<Object> detail;
}
