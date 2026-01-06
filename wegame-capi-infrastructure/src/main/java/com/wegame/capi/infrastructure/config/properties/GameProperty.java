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
    @Value("${game.enable.Bakunawa: false}")
    private boolean enableBakunawa;
    @Value("${game.enable.Lamang: false}")
    private boolean enableLamang;
    @Value("${game.enable.DiwataMayari: false}")
    private boolean enableDiwataMayari;
    @Value("${game.enable.Kapre: false}")
    private boolean enableKapre;
    @Value("${game.enable.Mangkukulam: false}")
    private boolean enableMangkukulam;
    @Value("${game.enable.Sirena: false}")
    private boolean enableSirena;
    @Value("${game.enable.Atlantis: false}")
    private boolean enableAtlantis;

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
        if (enableBakunawa) {
            gameTypeList.add(GameTypeEnum.BAKUNAWA);
        }
        if (enableLamang) {
            gameTypeList.add(GameTypeEnum.LAM_ANG);
        }
        if (enableDiwataMayari) {
            gameTypeList.add(GameTypeEnum.DIWATA_MAYARI);
        }
        if (enableKapre) {
            gameTypeList.add(GameTypeEnum.KAPRE);
        }
        if (enableMangkukulam) {
            gameTypeList.add(GameTypeEnum.MANGKUKULAM);
        }
        if (enableSirena) {
            gameTypeList.add(GameTypeEnum.SIRENA);
        }
        if (enableAtlantis) {
            gameTypeList.add(GameTypeEnum.ATLANTIS);
        }
        return gameTypeList;
    }

    public String enabledGameNames() {
        List<String> gameNameList = Optional.ofNullable(enabledGameTypes())
                .orElse(Collections.emptyList())
                .stream()
                .map(GameTypeEnum::getName)
                .collect(Collectors.toList());
        return String.join(",", gameNameList);
    }
}
