package com.wegame.capi.infrastructure.cache;

import com.wegame.base.CacheConstants;
import com.wegame.base.cache.AbstractLocalCache;
import com.wegame.base.enums.CommonCacheEnvEnum;
import org.springframework.stereotype.Component;

import static com.wegame.base.enums.CommonCacheEnvEnum.BET_RECORD;

/**
 * @author apple
 * 用户缓存
 */
@Component(CacheConstants.BET_RECORD)
public class BetRecordCache extends AbstractLocalCache {

    // todo 流量调度路由策略： 一致性hash

    public BetRecordCache() {
        super(CommonCacheEnvEnum.BET_RECORD.cacheKeyPrefixName(),
                60 * 5,
                10000000L);
    }


    //    public BetRecordCache() {
    //        super(BET_RECORD.cacheKeyPrefixName(), 300, 120, 100000L);
    //    }

    @Override
    public String getName() {
        return BET_RECORD.cacheName();
    }
}
