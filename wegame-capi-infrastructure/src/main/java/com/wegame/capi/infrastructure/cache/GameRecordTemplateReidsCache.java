package com.wegame.capi.infrastructure.cache;

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
@Component(CacheConstants.GAME_RECORD_TEMPLATE_REDIS)
public class GameRecordTemplateReidsCache extends AbstractLocalCache {

    public GameRecordTemplateReidsCache() {
        super(CommonCacheEnvEnum.GAME_RECORD_TEMPLATE_REDIS.cacheKeyPrefixName(), 365 * 24 * 60 * 60, 1000000);
    }

    @Override
    public String getName() {
        return CommonCacheEnvEnum.GAME_RECORD_TEMPLATE_REDIS.cacheName();
    }
}
