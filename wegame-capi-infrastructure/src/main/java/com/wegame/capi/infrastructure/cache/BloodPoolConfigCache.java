package com.wegame.capi.infrastructure.cache;

import com.wegame.base.CacheConstants;
import com.wegame.base.cache.AbstractCache;
import com.wegame.base.enums.CommonCacheEnvEnum;
import org.springframework.stereotype.Component;

import static com.wegame.base.enums.CommonCacheEnvEnum.BLOOD_POOL_CONFIG;
import static com.wegame.base.enums.CommonCacheEnvEnum.USER_ID;

/**
 * @author apple
 * 血池配置缓存
 */
@Component(CacheConstants.BLOOD_POOL_CONFIG)
public class BloodPoolConfigCache extends AbstractCache {

    public BloodPoolConfigCache() {
        super(BLOOD_POOL_CONFIG.cacheKeyPrefixName(), 60);
    }

    @Override
    public String getName() {
        return BLOOD_POOL_CONFIG.cacheName();
    }
}
