package com.wegame.capi.biz.domain.biz.bet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MalakasBetDo  {
    /**
     * 投注金额
     */
    private Long betAmount;
    /**
     * 投注记录id
     */
    private Long betRecordId;
    /**
     * 免费轮记录id
     */
    private Integer freeSpinsId;
    /**
     * 支付金额
     */
    private Long payAmount;
}
