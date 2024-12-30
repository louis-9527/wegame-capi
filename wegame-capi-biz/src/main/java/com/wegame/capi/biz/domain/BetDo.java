package com.wegame.capi.biz.domain;

import com.wegame.capi.common.enums.BetTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BetDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 游戏Id
     */
    private Long gameId;
    /**
     * 投注类型: @see GameBetTypeEnum
     */
    private BetTypeEnum betType;
    /**
     * 投注详情
     */
    private Object betDetail;
}

