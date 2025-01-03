package com.wegame.capi.biz.domain.biz.reward;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 中大奖结果do
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PushGrandPrizeResultDo implements Serializable {
    private static final long serialVersionUID = -22633876654348L;
    /**
     * 游戏类型
     *
     * @see com.wegame.capi.client.enums.GameTypeEnum
     */
    private String gameTypeId;
    /**
     * 中奖用户id
     */
    private Long userId;
    /**
     * 中奖用户名称
     */
    private String userName;
    /**
     * 中大奖类型
     *
     * @see com.wegame.capi.common.enums.PushGrandPrizeTypeEnum
     */
    private String grandPrizeType;
    /**
     * 中奖时间戳
     */
    private Long grandTime;
    /**
     * 推送文案
     */
    public String tipsContent;

}
