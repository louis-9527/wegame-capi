package com.wegame.capi.infrastructure.cache;

import com.wegame.base.CacheConstants;
import com.wegame.base.cache.AbstractCache;
import org.springframework.stereotype.Component;

import static com.wegame.base.enums.CommonCacheEnvEnum.GAME_DYNAMIC_OPERATION;


@Component(CacheConstants.GAME_DYNAMIC_OPERATION)
public class OperationEventConfigCache extends AbstractCache {
    @Override
    public String getName() {
        return GAME_DYNAMIC_OPERATION.cacheName();
    }

    public OperationEventConfigCache() {
        super(GAME_DYNAMIC_OPERATION.cacheKeyPrefixName(), 60*60*24);
    }

}
