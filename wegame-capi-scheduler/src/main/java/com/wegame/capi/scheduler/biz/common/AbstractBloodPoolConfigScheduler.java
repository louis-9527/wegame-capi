package com.wegame.capi.scheduler.biz.common;

import com.wegame.base.exception.LockException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.domain.res.singlewallet.GetPoolConfigRes;
import com.wegame.capi.biz.config.domain.common.CommonBloodPoolConfig;
import com.wegame.capi.biz.service.SingleWalletConfigService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.infrastructure.config.properties.GameApiProperty;
import com.wegame.capi.scheduler.AbstractScheduler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;

/**
 * 血池配置任务
 */
@Slf4j
public abstract class AbstractBloodPoolConfigScheduler
        extends AbstractScheduler
        implements Runnable {

    @Autowired
    GameApiProperty gameApiProperty;
    @Autowired
    SingleWalletConfigService singleWalletConfigService;

    public abstract GameTypeEnum gameTypeEnum();

    public abstract CommonBloodPoolConfig bloodPoolConfig();

    @Override
    public void run() {
        try {
            log.info("AbstractBloodPoolConfigScheduler start");
            CommonBloodPoolConfig config = bloodPoolConfig();
            Map<String, List<String>> supportMap = config.getSupportMap();
            supportMap.forEach((platformId, channelIds) -> {
                channelIds.forEach(channelId -> {
                    GameApiProperty.PrivateInfo privateInfo = gameApiProperty.getDosPrivateInfo(channelId);
                    try {
                        if (privateInfo.isDosSupport()) {
                            List<GetPoolConfigRes.BloodPoolConfig> list = singleWalletConfigService.getBloodPoolConfigNoCache(privateInfo.getPlatformId(), privateInfo.getChannelId(), gameTypeEnum());
                            if (CollectionUtils.isNotEmpty(list)) {
                                singleWalletConfigService.putBloodPoolConfigCache(privateInfo.getPlatformId(), privateInfo.getChannelId(), gameTypeEnum(), list);
                            }
                        }
                    } catch (Exception e) {
                        log.error("cache blood config error, pid: {}, channelId: {}, gameId: {}",
                                privateInfo.getPlatformId(),
                                privateInfo.getChannelId(),
                                gameTypeEnum().getId(),
                                e);
                    }
                });

            });
        } catch (Exception e) {
            log.error("AbstractBloodConfigScheduler run exception", e);
        } finally {
            log.info("AbstractBloodPoolConfigScheduler end");
        }
    }
}
