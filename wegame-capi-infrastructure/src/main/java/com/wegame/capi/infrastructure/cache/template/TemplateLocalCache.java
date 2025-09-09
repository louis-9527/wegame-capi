package com.wegame.capi.infrastructure.cache.template;

import com.wegame.base.CacheConstants;
import com.wegame.base.cache.AbstractLocalCache;
import com.wegame.base.enums.CommonCacheEnvEnum;
import org.springframework.stereotype.Component;

/**
 * @author apple
 * gameRecord Tempate 缓存
 * key:betRecordTemplateId
 * value:gameRecordTemplate--List
 */
@Component(CacheConstants.TEMPLATE_LOCAL)
public class TemplateLocalCache extends AbstractLocalCache {

    public TemplateLocalCache() {
        super(CommonCacheEnvEnum.GAME_RECORD_TEMPLATE_LOCAL.cacheKeyPrefixName(), CommonCacheEnvEnum.GAME_RECORD_TEMPLATE_LOCAL.expirationSeconds(), 250000);
    }

    @Override
    public String getName() {
        return CommonCacheEnvEnum.GAME_RECORD_TEMPLATE_LOCAL.cacheName();
    }
}
