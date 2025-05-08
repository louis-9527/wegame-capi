package com.wegame.capi.infrastructure.cache;

import com.wegame.base.CacheConstants;
import com.wegame.base.cache.AbstractCache;
import com.wegame.base.cache.AbstractLocalCache;
import com.wegame.base.enums.CommonCacheEnvEnum;
import org.springframework.stereotype.Component;

import static com.wegame.base.enums.CommonCacheEnvEnum.USER_INFO;

/**
 * @author apple
 * 用户缓存
 */
@Component(CacheConstants.USER_INFO)
public class UserCache extends AbstractLocalCache {

    public UserCache() {
        super(CommonCacheEnvEnum.USER_INFO.cacheKeyPrefixName(), 60, 6000000);
    }

    @Override
    public String getName() {
        return USER_INFO.cacheName();
    }
}
