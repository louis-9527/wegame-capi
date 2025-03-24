package com.wegame.capi.biz.domain.biz.operation;

import com.wegame.capi.client.enums.BetTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 干预事件配置Do
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OperationEventConfigDo {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 游戏ID
     */
    private String gameId;
    /**
     * 投注类型
     */
    private BetTypeEnum betType;
    /**
     * 倍数
     */
    private Double multiple;
}
