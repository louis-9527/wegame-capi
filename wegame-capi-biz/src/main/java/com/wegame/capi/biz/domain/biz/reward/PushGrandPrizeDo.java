package com.wegame.capi.biz.domain.biz.reward;

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
public class PushGrandPrizeDo<T> implements Serializable {
    private static final long serialVersionUID = -22633876654348L;
    /**
     * 游戏类型
     *
     * @see com.wegame.capi.client.enums.GameTypeEnum
     */
    private String gameTypeId;
    /**
     * 中大奖数据
     */
    private T data;

}
