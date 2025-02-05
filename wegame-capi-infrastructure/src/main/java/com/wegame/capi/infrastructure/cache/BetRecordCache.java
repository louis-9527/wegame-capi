package com.wegame.capi.infrastructure.cache;

import com.wegame.base.CacheConstants;
import com.wegame.base.cache.AbstractLocalCache;
import com.wegame.base.cache.AbstractMultiCache;
import com.wegame.base.enums.CommonCacheEnvEnum;
import org.springframework.stereotype.Component;

import static com.wegame.base.enums.CommonCacheEnvEnum.BET_RECORD;

/**
 * @author apple
 * 投注记录缓存
 */
@Component(CacheConstants.BET_RECORD)
public class BetRecordCache extends AbstractMultiCache {


    public BetRecordCache() {
        super(BET_RECORD.cacheKeyPrefixName(), 60, 15, 10000L);
    }

    @Override
    public String getName() {
        return BET_RECORD.cacheName();
    }
}
