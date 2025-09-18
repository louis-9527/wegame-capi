package com.wegame.capi.test.biz.innerdos.metrics;

import com.google.common.collect.Lists;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.metrics.GameMetricsDo;
import com.wegame.capi.biz.domain.metrics.UserPlayMetricsDo;
import com.wegame.capi.biz.service.metrics.DefaultMetricsService;
import com.wegame.capi.biz.service.metrics.GameMetricsDataService;
import com.wegame.capi.biz.service.metrics.UserPlayMetricsDataService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.innerdos.MetricsTypeEnum;
import com.wegame.capi.infrastructure.config.ThreadPoolConfig;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.Assert.*;

@Slf4j
public class DefaultMetricsServiceTest extends BaseTest {
    @Autowired
    private DefaultMetricsService defaultMetricsService;
    @Autowired
    private GameMetricsDataService gameMetricsDataService;
    @Autowired
    private UserPlayMetricsDataService userPlayMetricsDataService;
    @Autowired
    @Qualifier(ThreadPoolConfig.innerDosThreadPoolExecutor)
    private ThreadPoolExecutor executor;

    @Test
    public void metrics_normal_test() {
        // exInfo 这里代表是否有免费轮
        boolean metricsFlag = defaultMetricsService.metrics(MetricsManagerTestMocker.betRecordDo_normal(GameTypeEnum.IBONG_ADARNA), MetricsTypeEnum.IBONG_ADARNA, false);
        // 等一会儿，让定时任务执行
        LockSupport.parkNanos(this, TimeUnit.SECONDS.toNanos(120));
        assertTrue(metricsFlag);
    }

    // 大盘采集并发请看下测试
    @Test
    public void metrics_current_test() {
        // exInfo 这里代表是否有免费轮
        // 1.查询出当前已经有多少金额
        GameMetricsDo gameMetricsDoPre = gameMetricsDataService.query("50", "1050", MetricsTypeEnum.IBONG_ADARNA);
        UserPlayMetricsDo userPlayMetricsDoPre = userPlayMetricsDataService.query("50", "test001", "1050", MetricsTypeEnum.IBONG_ADARNA);
        long gameTotalBetAmount = gameMetricsDoPre.getTotalBetAmount();
        long gameTotalPayoutAmount = gameMetricsDoPre.getTotalPayoutAmount();
        long userTotalBetAmount = userPlayMetricsDoPre.getTotalBetAmount();
        long userTotalPayoutAmount = userPlayMetricsDoPre.getTotalPayoutAmount();


        List<Future> futureList = Lists.newArrayList();

        for (int i = 0; i < 100; i++) {
            BetRecordDo betRecordDo = MetricsManagerTestMocker.betRecordDo_normal(GameTypeEnum.IBONG_ADARNA);
            Future<?> future = executor.submit(() -> defaultMetricsService.metrics(betRecordDo, MetricsTypeEnum.IBONG_ADARNA, false));
            futureList.add(future);
            gameTotalBetAmount = gameTotalBetAmount + betRecordDo.getPayAmount();
            gameTotalPayoutAmount = gameTotalPayoutAmount + betRecordDo.getPayOutAmount();
            userTotalBetAmount = userTotalBetAmount + betRecordDo.getPayAmount();
            userTotalPayoutAmount = userTotalPayoutAmount + betRecordDo.getPayOutAmount();
        }

        futureList.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        // 等待1分钟，让定时任务走完
        LockSupport.parkNanos(this, TimeUnit.SECONDS.toNanos(60 * 2));

        // 再次获取存储的数据
        GameMetricsDo gameMetricsDoPost = gameMetricsDataService.query("50", "1050", MetricsTypeEnum.IBONG_ADARNA);
        UserPlayMetricsDo userPlayMetricsDoPost = userPlayMetricsDataService.query("50", "test001", "1050", MetricsTypeEnum.IBONG_ADARNA);

        log.info("初始存储的数据,gameTotalBetAmount:{},gameTotalPayoutAmount:{},userTotalBetAmount:{},userTotalPayoutAmount:{}",
                gameMetricsDoPre.getTotalBetAmount(),
                gameMetricsDoPre.getTotalPayoutAmount(),
                userPlayMetricsDoPre.getTotalBetAmount(),
                userPlayMetricsDoPre.getTotalPayoutAmount());

        log.info("并发后真实统计的数据,gameTotalBetAmount:{},gameTotalPayoutAmount:{},userTotalBetAmount:{},userTotalPayoutAmount:{}",
                gameTotalBetAmount,
                gameTotalPayoutAmount,
                userTotalBetAmount,
                userTotalPayoutAmount);

        log.info("并发后存储中的数据,gameTotalBetAmount:{},gameTotalPayoutAmount:{},userTotalBetAmount:{},userTotalPayoutAmount:{}",
                gameMetricsDoPost.getTotalBetAmount(),
                gameMetricsDoPost.getTotalPayoutAmount(),
                userPlayMetricsDoPost.getTotalBetAmount(),
                userPlayMetricsDoPost.getTotalPayoutAmount());
    }


}
