package com.wegame.capi.client.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("正常投注返回")
@EqualsAndHashCode(callSuper = false)
public class GameBetPayOutDetailVo implements Serializable {
    private static final long serialVersionUID = 6487808344235380369L;
    private BigDecimal multiple;
    /**
     * 赔率
     */
    private BigDecimal odds;
    private List<SymbolIndexVo> rewardIndex;
    /**
     * 当前中奖线的中奖id列表
     */
    private List<String> rewardSymbolIdList;
    private Long payOutAmount;
}

