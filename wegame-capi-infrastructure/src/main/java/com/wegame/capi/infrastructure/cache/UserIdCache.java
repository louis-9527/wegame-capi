package com.wegame.capi.infrastructure.cache;

import com.wegame.base.CacheConstants;
import com.wegame.base.cache.AbstractCache;
import com.wegame.base.cache.AbstractLocalCache;
import com.wegame.base.enums.CommonCacheEnvEnum;
import org.springframework.stereotype.Component;

import static com.wegame.base.enums.CommonCacheEnvEnum.USER_ID;
import static com.wegame.base.enums.CommonCacheEnvEnum.USER_INFO;

/**
 * @author apple
 * 用户ID缓存
 */
@Component(CacheConstants.USER_ID)
public class UserIdCache extends AbstractCache {

    public UserIdCache() {
        super(CommonCacheEnvEnum.USER_ID.cacheKeyPrefixName(), 30 * 24 * 60 * 60);
    }

    @Override
    public String getName() {
        return USER_ID.cacheName();
    }
}
