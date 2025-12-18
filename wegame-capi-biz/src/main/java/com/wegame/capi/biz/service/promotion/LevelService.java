package com.wegame.capi.biz.service.promotion;

import com.wegame.capi.api.domain.res.promotion.RedeemTreasureRes;
import com.wegame.capi.api.domain.res.promotion.level.LevelConfigRes;
import com.wegame.capi.api.domain.res.promotion.level.PlayerLevelRes;
import com.wegame.capi.api.promotion.LevelApi;
import com.wegame.capi.biz.builder.req.level.LevelReqBuilder;
import com.wegame.capi.biz.converter.promotion.level.LevelConfigConverter;
import com.wegame.capi.biz.converter.promotion.level.PlayerLevelConverter;
import com.wegame.capi.biz.domain.biz.promotion.level.LevelConfigDo;
import com.wegame.capi.biz.domain.biz.promotion.level.PlayerLevelDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 宝箱service
 */
@Component
public class LevelService {

    @Autowired
    LevelApi levelApi;

    public LevelConfigDo getLevelConfig() {
        LevelConfigRes res = levelApi.getLevelConfig(LevelReqBuilder.buildLevelConfigReq());
        return LevelConfigConverter.resToDo(res);
    }

    public PlayerLevelDo getPlayerLevel() {
        PlayerLevelRes res = levelApi.getPlayerLevel(LevelReqBuilder.buildPlayerLevelReq());
        return PlayerLevelConverter.resToDo(res);
    }
}
