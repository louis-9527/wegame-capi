package com.wegame.capi.biz.manager;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.lock.DistributeLock;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.service.metrics.DefaultMetricsService;
import com.wegame.capi.biz.service.metrics.MetricsService;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.client.enums.innerdos.MetricsTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.infrastructure.config.ThreadPoolConfig;
import com.wegame.capi.infrastructure.config.properties.GameInnerDosProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

import static com.wegame.capi.common.Constants.OPT_BUSY;

/**
 * 指标采集处理管理
 * 由此服务分发采集服务
 */
@Slf4j
@Component
public class MetricsManager implements InitializingBean, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private final Map<MetricsTypeEnum, MetricsService> metricsServiceMap = new HashMap<>();
    @Autowired
    @Qualifier("defaultMetricsService")
    private MetricsService defaultMetricsService;
    @Autowired
    private GameInnerDosProperties gameInnerDosProperties;
    @Autowired
    @Qualifier(ThreadPoolConfig.innerDosThreadPoolExecutor)
    private ThreadPoolExecutor executor;
    @Autowired
    private DistributeLock distributeLock;

    /**
     * 执行采集指标
     * 内部异步
     *
     * @param betRecordDo betRecordDo
     * @param metricsType metricsType
     * @param extInfo     extInfo
     * @return true/false
     */
    public boolean metrics(BetRecordDo betRecordDo, MetricsTypeEnum metricsType, SingleWalletBetResultDo betResultDo, Object extInfo) {
        // 准入
        if (!this.canMetrics(betRecordDo, metricsType, betResultDo, extInfo)) {
            log.info("MetricsManager#metrics.game inner dos not enable:{}", metricsType);
            return false;
        }
        // async
        executor.execute(() -> {
            // 没有定制化，则走默认
            MetricsService metricsService = Optional.ofNullable(metricsServiceMap.get(metricsType)).orElse(defaultMetricsService);
            if (metricsService instanceof DefaultMetricsService) {
                log.info("MetricsManager#metrics no match metricsService,use defaultMetricsService.metricsType:{}", metricsType);
            }

            distributeLock.tryLockThenCall(LockNameUtil.getMetricsLockName(metricsType.getGameType()),
                    2000,
                    3000,
                    () -> metricsService.metrics(betRecordDo, metricsType, extInfo),
                    OPT_BUSY);
        });
        return true;
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, MetricsService> beansOfType = applicationContext.getBeansOfType(MetricsService.class);
        if (CollectionUtil.isNotEmpty(beansOfType)) {
            beansOfType.values().forEach(metricsService -> {
                if (Objects.nonNull(metricsService.metricsType())) {
                    metricsServiceMap.put(metricsService.metricsType(), metricsService);
                }
            });
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private boolean canMetrics(BetRecordDo betRecordDo, MetricsTypeEnum metricsType, SingleWalletBetResultDo betResultDo, Object extInfo) {
        return this.gameEnable(metricsType) && this.betSuccess(betResultDo) && canMetricsPlayType(betRecordDo);
    }

    /**
     * 属于采集范围.dos || template
     *
     * @param betRecordDo begRecordDo
     * @return true/false
     */
    private boolean canMetricsPlayType(BetRecordDo betRecordDo) {
        return Objects.equals(betRecordDo.getGamePlayType(), GamePlayTypeEnum.EVENT_PLAY)
                || Objects.equals(betRecordDo.getGamePlayType(), GamePlayTypeEnum.TEMPLATE_PLAY);
    }

    /**
     * 投注是否成功
     *
     * @param betResultDo betResultDo
     * @return true/false
     */
    private boolean betSuccess(SingleWalletBetResultDo betResultDo) {
        return Objects.equals(betResultDo.getBetStatus(), BetStatusEnum.SUCCESS);
    }

    /**
     * game维度的采集开关是否开启
     *
     * @param metricsType metricsType
     * @return true/false
     */
    private boolean gameEnable(MetricsTypeEnum metricsType) {
        return gameInnerDosProperties.getEnableGameList().contains(metricsType);
    }
}
