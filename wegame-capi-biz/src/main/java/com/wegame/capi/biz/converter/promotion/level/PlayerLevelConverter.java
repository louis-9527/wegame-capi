package com.wegame.capi.biz.converter.promotion.level;

import com.wegame.capi.api.domain.res.promotion.RedeemTreasureRes;
import com.wegame.capi.api.domain.res.promotion.level.PlayerLevelRes;
import com.wegame.capi.biz.domain.biz.promotion.RedeemTreasureDo;
import com.wegame.capi.biz.domain.biz.promotion.level.PlayerLevelDo;
import com.wegame.capi.client.domain.vo.promotion.RedeemTreasureVo;
import com.wegame.capi.client.domain.vo.promotion.level.PlayerLevelVo;

import java.util.Objects;

public class PlayerLevelConverter {

    public static PlayerLevelDo resToDo(PlayerLevelRes res) {
        if (Objects.isNull(res)) {
            return null;
        }
        return PlayerLevelDo.builder()
                .playerLevel(res.getPlayerLevel())
                .playerName(res.getPlayerName())
                .playerExperience(res.getPlayerExperience())
                .refId(res.getRefId())
                .build();
    }

    public static PlayerLevelVo doToVo(PlayerLevelDo playerLevelDo) {
        if (Objects.isNull(playerLevelDo)) {
            return null;
        }
        return PlayerLevelVo.builder()
                .refId(playerLevelDo.getRefId())
                .playerName(playerLevelDo.getPlayerName())
                .playerLevel(playerLevelDo.getPlayerLevel())
                .playerExperience(playerLevelDo.getPlayerExperience())
                .build();
    }
}
