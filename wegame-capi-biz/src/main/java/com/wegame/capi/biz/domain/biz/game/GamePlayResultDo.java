package com.wegame.capi.biz.domain.biz.game;

import com.wegame.capi.biz.domain.biz.GameInfoDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelRewardDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
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
    private GameInfoDo gameInfo;
    /**
     * 游戏滚轮中奖列表
     */
    private List<WheelRewardDo> wheelRewardList;

    /**
     * 是否中奖
     * @return
     */
    public boolean hasReward() {
        return CollectionUtils.isNotEmpty(wheelRewardList);
    }

    /**
     * 是否中奖免费轮
     * @return
     */
    public boolean hasRewardFreeSpins() {
        return CollectionUtils.isNotEmpty(wheelRewardList) && wheelRewardList
                .stream()
                .filter(item -> item.getRewardResult().isFreeSpin())
                .count() > 0;
    }


}

