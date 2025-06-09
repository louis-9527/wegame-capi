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
    @Value("${game.enable.Malakas}")
    private boolean enableMalakas;
    @Value("${game.enable.IbongAdarna}")
    private boolean enableIbongAdarna;
    @Value("${game.enable.Lamang}")
    private boolean enableLamang;

    public List<GameTypeEnum> enabledGameTypes() {
        List<GameTypeEnum> gameTypeList = new ArrayList<>();
        if (enableMariaMaking) {
            gameTypeList.add(GameTypeEnum.MARIA_MAKILING);
        }
        if (enableMalakas) {
            gameTypeList.add(GameTypeEnum.MALAKAS);
        }
        if (enableIbongAdarna) {
            gameTypeList.add(GameTypeEnum.IBONG_ADARNA);
        }
        if (enableLamang) {
            gameTypeList.add(GameTypeEnum.LAM_ANG);
        }
        return gameTypeList;
    }
}
