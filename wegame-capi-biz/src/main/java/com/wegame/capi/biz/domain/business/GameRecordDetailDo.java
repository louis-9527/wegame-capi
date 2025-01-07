package com.wegame.capi.biz.domain.business;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * game_record_detail：游戏记录明细
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameRecordDetailDo implements Serializable {
    private static final long serialVersionUID = -509759714931949568L;
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
    private String gameResult;
    /**
     * 中奖集合json
     */
    private String winResult;
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

