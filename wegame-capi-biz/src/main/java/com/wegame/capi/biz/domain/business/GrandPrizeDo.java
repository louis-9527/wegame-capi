package com.wegame.capi.biz.domain.business;

import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.GrandPrizeTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 中大奖do
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GrandPrizeDo implements Serializable {
    private static final long serialVersionUID = -22633876654348L;
    /**
     * 游戏类型
     *
     * @see GameTypeEnum
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
     * @see GrandPrizeTypeEnum
     */
    private String grandPrizeType;
    /**
     * 中奖时间戳
     */
    private Long grandTime;

}
