package com.wegame.capi.biz.domain.biz.operation;

import com.wegame.capi.client.enums.BetTypeEnum;
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
