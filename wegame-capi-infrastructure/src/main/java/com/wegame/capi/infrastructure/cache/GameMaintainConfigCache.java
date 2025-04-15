package com.wegame.capi.infrastructure.cache;

import com.wegame.base.cache.AbstractLocalCache;
import com.wegame.base.enums.CommonCacheEnvEnum;
import org.springframework.stereotype.Component;

import static com.wegame.base.CacheConstants.GAME_MAINTAIN_CONFIG;

@Component(GAME_MAINTAIN_CONFIG)
public class GameMaintainConfigCache extends AbstractLocalCache {
    public GameMaintainConfigCache() {
        super(CommonCacheEnvEnum.GAME_MAINTAIN_CONFIG.cacheKeyPrefixName(), 15, 10);
    }

    @Override
    public String getName() {
        return CommonCacheEnvEnum.GAME_MAINTAIN_CONFIG.cacheName();
    }
}
