package com.wegame.capi.infrastructure.config.properties;

import com.wegame.capi.client.enums.GameTypeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
public class GameProperty {
    // 增加默认值 ！！！ 增加默认值 ！！！ 增加默认值 ！！！
    @Value("${game.enable.MariaMakiling: false}")
    private boolean enableMariaMaking;
    @Value("${game.enable.Malakas: false}")
    private boolean enableMalakas;
    @Value("${game.enable.IbongAdarna: false}")
    private boolean enableIbongAdarna;
    @Value("${game.enable.DiwataMayari: false}")
    private boolean enableDiwataMayari;

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
        if (enableDiwataMayari) {
            gameTypeList.add(GameTypeEnum.DIWATA_MAYARI);
        }
        return gameTypeList;
    }

    public String enabledGameNames() {
        List<GameTypeEnum> gameTypeList = new ArrayList<>();
        if (enableMariaMaking) {
            gameTypeList.add(GameTypeEnum.MARIA_MAKILING);
        }
        List<String> gameNameList = Optional.ofNullable(gameTypeList)
                .orElse(Collections.emptyList())
                .stream()
                .map(GameTypeEnum::getName)
                .collect(Collectors.toList());
        return String.join(",", gameNameList);
    }
}
