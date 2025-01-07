package com.wegame.capi.dao.mapper.domain.business;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 游戏记录明细
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameRecordDetailPo<G, W> implements Serializable {
    private static final long serialVersionUID = -1774298028919031808L;
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 投注金额 单位：分
     */
    private Long betAmount;
    /**
     * 派彩金额 单位：分
     */
    private Long payOutAmount;
    /**
     * 游戏结果json
     */
    private G gameResult;
    /**
     * 中奖集合json
     */
    private W winResult;
    /**
     * 赔率
     */
    private BigDecimal odds;
    /**
     * 倍数
     */
    private Integer multiple;

   ///////////////  above auto generated ///////////////
}

