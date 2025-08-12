package com.wegame.capi.infrastructure.cache;

import com.wegame.base.CacheConstants;
import com.wegame.base.cache.AbstractMultiCache;
import com.wegame.base.enums.CommonCacheEnvEnum;
import org.springframework.stereotype.Component;

/**
 * betRecord 二级缓存
 * key: [起始倍数，终点倍数)
 * value: BetRecordTemplate Id ---list
 */
@Component(CacheConstants.BET_RECORD_TEMPLATE)
public class BetRecordTemplateCache extends AbstractMultiCache {
    public BetRecordTemplateCache() {
        super(CommonCacheEnvEnum.BET_RECORD_TEMPLATE.cacheKeyPrefixName(), 10 * 60, 300, 20);
    }

    @Override
    public String getName() {
        return CommonCacheEnvEnum.BET_RECORD_TEMPLATE.cacheName();
    }
}
