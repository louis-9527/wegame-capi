package com.wegame.capi.infrastructure.cache;

import com.wegame.base.CacheConstants;
import com.wegame.base.cache.AbstractMultiCache;
import org.springframework.stereotype.Component;

import static com.wegame.base.enums.CommonCacheEnvEnum.*;

/**
 * @author apple
 * 用户缓存
 */
@Component(CacheConstants.BET_RECORD)
public class BetRecordCache extends AbstractMultiCache {

    public BetRecordCache() {
        super(BET_RECORD.cacheKeyPrefixName(), 100, 50, 10000L);
    }

    @Override
    public String getName() {
        return BET_RECORD.cacheName();
    }
}
