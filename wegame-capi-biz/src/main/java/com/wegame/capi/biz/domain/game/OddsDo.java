package com.wegame.capi.biz.domain.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 赔率do
 *
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OddsDo implements Serializable {
    private static final long serialVersionUID = -2098745454848L;

    /**
     * 游戏id
     */
    private Long gameId;

    /**
     * 元素id
     */
    private Long symbolId;

    /**
     * 元素连数
     */
    private Integer linkCount;

    /**
     * 投注类型
     */
    private Integer betType;

}
