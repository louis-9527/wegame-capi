package com.wegame.capi.dao.mapper.domain.user;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * game_record_detail：游戏记录明细
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameRecordDetailPo implements Serializable {
    private static final long serialVersionUID = -8545790273466368L;
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
    private Long rewardAmount;
    /**
     * 游戏结果json
     */
    private String gameResult;
    /**
     * 中奖结果json
     */
    private String rewardResult;
    /**
     * 倍数
     */
    private Integer multiple;

   ///////////////  above auto generated ///////////////
}

