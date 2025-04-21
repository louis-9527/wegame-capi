package com.wegame.capi.infrastructure.cache;

import com.wegame.base.CacheConstants;
import com.wegame.base.cache.AbstractCache;
import org.springframework.stereotype.Component;

import static com.wegame.base.enums.CommonCacheEnvEnum.GAME_RECORD;

/**
 * @author apple
 * 投注记录缓存
 */
@Component(CacheConstants.GAME_RECORD)
public class GameRecordCache extends AbstractCache {

    public GameRecordCache() {
        super(GAME_RECORD.cacheKeyPrefixName(), 300);
    }

    @Override
    public String getName() {
        return GAME_RECORD.cacheName();
    }
}
