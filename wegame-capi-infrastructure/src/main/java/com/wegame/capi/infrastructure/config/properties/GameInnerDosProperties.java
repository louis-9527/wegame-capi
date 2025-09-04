package com.wegame.capi.infrastructure.config.properties;

import com.google.common.collect.Lists;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.MetricsTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

/**
 * 内部干预开启开关
 * 游戏维度
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = GameInnerDosProperties.GAME_INNER_DOS_PROPERTIES_PREFIX)
public class GameInnerDosProperties {
    public static final String GAME_INNER_DOS_PROPERTIES_PREFIX = "game.inner.dos";

    private List<MetricsTypeEnum> enableGameList;

    public List<MetricsTypeEnum> getEnableGameList() {
        return Optional.ofNullable(this.enableGameList).orElse(Lists.newArrayList());
    }
}
