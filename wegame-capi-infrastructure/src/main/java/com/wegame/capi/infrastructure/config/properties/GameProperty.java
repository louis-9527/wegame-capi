package com.wegame.capi.infrastructure.config.properties;

import com.wegame.capi.client.enums.GameTypeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class GameProperty {

    @Value("${game.enable.MariaMakiling}")
    private boolean enableMariaMaking;

    public List<GameTypeEnum> enabledGameTypes() {
        List<GameTypeEnum> gameTypeList = new ArrayList<>();
        if (enableMariaMaking) {
            gameTypeList.add(GameTypeEnum.MARIA_MAKILING);
        }
        return gameTypeList;
    }
}
