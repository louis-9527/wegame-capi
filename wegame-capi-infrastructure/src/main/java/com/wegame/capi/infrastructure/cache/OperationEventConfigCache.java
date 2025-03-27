package com.wegame.capi.infrastructure.cache;

import com.wegame.base.CacheConstants;
import com.wegame.base.cache.AbstractCache;
import org.springframework.stereotype.Component;

import static com.wegame.base.enums.CommonCacheEnvEnum.OPERATION_EVENT_CONFIG;

@Component(CacheConstants.OPERATION_EVENT_CONFIG)
public class OperationEventConfigCache extends AbstractCache {

    @Override
    public String getName() {
        return OPERATION_EVENT_CONFIG.cacheName();
    }

    public OperationEventConfigCache() {
        super(OPERATION_EVENT_CONFIG.cacheKeyPrefixName(), 60 * 60 * 24);
    }

}
