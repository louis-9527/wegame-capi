package com.wegame.capi.infrastructure.cache.template;

import com.wegame.base.CacheConstants;
import com.wegame.base.cache.AbstractCache;
import com.wegame.base.enums.CommonCacheEnvEnum;
import org.springframework.stereotype.Component;

/**
 * gameRecord Template 缓存
 * key:betRecordTemplateId
 * value:gameRecordTemplate--List
 */
@Component(CacheConstants.TEMPLATE_REDIS)
public class TemplateRedisCache extends AbstractCache {

    public TemplateRedisCache() {
        super(CommonCacheEnvEnum.TEMPLATE_REDIS.cacheKeyPrefixName(), CommonCacheEnvEnum.TEMPLATE_REDIS.expirationSeconds());
    }

    @Override
    public String getName() {
        return CommonCacheEnvEnum.TEMPLATE_REDIS.cacheName();
    }
}
