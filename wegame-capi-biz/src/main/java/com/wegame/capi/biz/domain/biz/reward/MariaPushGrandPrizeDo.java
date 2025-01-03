package com.wegame.capi.biz.domain.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * maria推送中大奖do
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MariaPushGrandPrizeDo implements Serializable {
    private static final long serialVersionUID = -22633876654348L;
    /**
     * 中奖用户id
     */
    private Long userId;
    /**
     * 中奖用户名称
     */
    private String userName;
    /**
     * 游戏记录id
     */
    private Long gameRecordId;
    /**
     * 免费轮次数
     */
    private Integer freeSpinsCount;

    /**
     * 免费轮中奖总金额
     */
    private Long freeSpinsTotalAmount;

}
