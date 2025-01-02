package com.wegame.capi.biz.domain;

import com.wegame.capi.biz.domain.game.RewardResultDo;
import com.wegame.capi.common.enums.GameTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 玩游戏结果模型
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GamePlayResultDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 游戏类型
     */
    private GameTypeEnum gameType;
    /**
     * 游戏信息
     */
    private GameInfoDo gameInfoDo;

    private List<SymbolDo[][]> symbolArrList;


    private RewardResultDo rewardResult;  // todo
    // todo 其他
}

