package com.wegame.capi.biz.domain.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 中奖规则do
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RewardRuleDo implements Serializable {
    private static final long serialVersionUID = -2266308273445L;
    /**
     * 游戏id
     */
    private String gameId;
    /**
     * 投注类型
     */
    private Integer betType;

    /**
     * 滚轮方格分布
     */
    private Integer[][] rollerGrid;


}
