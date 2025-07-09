package com.wegame.capi.biz.listener;

import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.SingleWalletFreeSpinApi;
import com.wegame.capi.api.base.CommonBaseApi;
import com.wegame.capi.api.promotion.BagApi;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.infrastructure.GlobalInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.IntStream;

import static com.wegame.capi.infrastructure.config.rest.FreeSpinRestTemplateConfig.FREE_SPIN_CONNECTION_MANAGER_BEAN;
import static com.wegame.capi.infrastructure.config.rest.PromotionRestTemplateConfig.PROMOTION_CONNECTION_MANAGER_BEAN;
import static com.wegame.capi.infrastructure.config.rest.WalletRestTemplateConfig.WALLET_CONNECTION_MANAGER_BEAN;

@Slf4j
@Component
public class RestTemplateWarmUpHelper {

    // healthCheck
    @Autowired
    SingleWalletApi singleWalletApi;
    @Autowired
    SingleWalletFreeSpinApi singleWalletFreeSpinApi;
    @Autowired
    BagApi bagApi;
    // init Connections
    @Value("${api.game.singleDomain}")
    String singleDomain;
    @Value("${api.game.singleDomainIntConnections:50}")
    int singleDomainIntConnections;
    @Value("${api.game.freeSpinDomain}")
    String freeSpinDomain;
    @Value("${api.game.freeSpinDomainIntConnections:10}")
    int freeSpinDomainIntConnections;
    @Value("${api.game.promotionDomain}")
    String promotionDomain;
    @Value("${api.game.promotionDomainIntConnections:100}")
    int promotionDomainIntConnections;
    @Autowired
    @Qualifier(WALLET_CONNECTION_MANAGER_BEAN)
    PoolingHttpClientConnectionManager walletConnectionManager;
    @Autowired
    @Qualifier(FREE_SPIN_CONNECTION_MANAGER_BEAN)
    PoolingHttpClientConnectionManager freeSpinConnectionManager;
    @Autowired
    @Qualifier(PROMOTION_CONNECTION_MANAGER_BEAN)
    PoolingHttpClientConnectionManager promotionConnectionManager;
    @Autowired
    RandomService randomService;

    public void warmUpAllConnections() {
        //        int randomSeconds = randomService.randomInt(1, 60);
        //        LockSupport.parkNanos(this, TimeUnit.SECONDS.toNanos(randomSeconds));
        warmUpConnections(singleWalletApi, singleDomainIntConnections);
        //        randomSeconds = randomService.randomInt(1, 60);
        //        LockSupport.parkNanos(this, TimeUnit.SECONDS.toNanos(randomSeconds));
        warmUpConnections(singleWalletFreeSpinApi, freeSpinDomainIntConnections);
        //        randomSeconds = randomService.randomInt(1, 60);
        //        LockSupport.parkNanos(this, TimeUnit.SECONDS.toNanos(randomSeconds));
        warmUpConnections(bagApi, promotionDomainIntConnections);
    }

    public void keepAllConnectionPool() {
        try {
            log.info("walletConnectionManager ManagerStatus: {}", walletConnectionManager.getTotalStats());
            log.info("freeSpinConnectionManager ManagerStatus: {}", freeSpinConnectionManager.getTotalStats());
            log.info("promotionConnectionManager ManagerStatus: {}", promotionConnectionManager.getTotalStats());
            //            int randomSeconds = randomService.randomInt(1, 5);
            if (GlobalInstance.warmConnectionSwitch) {
                keepConnectionPool(walletConnectionManager, singleWalletApi, singleDomainIntConnections);
            }
            //            randomSeconds = randomService.randomInt(1, 5);
            //            LockSupport.parkNanos(this, TimeUnit.SECONDS.toNanos(randomSeconds));
            LockSupport.parkNanos(this, TimeUnit.SECONDS.toNanos(3));
            if (GlobalInstance.warmConnectionSwitch) {
                keepConnectionPool(freeSpinConnectionManager, singleWalletFreeSpinApi, freeSpinDomainIntConnections);
            }
            //            randomSeconds = randomService.randomInt(1, 5);
            //            LockSupport.parkNanos(this, TimeUnit.SECONDS.toNanos(randomSeconds));
            LockSupport.parkNanos(this, TimeUnit.SECONDS.toNanos(3));
            if (GlobalInstance.warmConnectionSwitch) {
                keepConnectionPool(promotionConnectionManager, bagApi, promotionDomainIntConnections);
            }
        } catch (Exception e) {
            log.error("keepAllConnectionPool error", e);
        }
    }

    //    private void initAllDomainConnections() {
    //        try {
    //            initConnections(walletConnectionManager, singleDomainIntConnections, HttpHost.create(singleDomain));
    //            initConnections(freeSpinConnectionManager, freeSpinDomainIntConnections, HttpHost.create(freeSpinDomain));
    //            initConnections(promotionConnectionManager, promotionDomainIntConnections, HttpHost.create(promotionDomain));
    //        } catch (Exception e) {
    //            log.error("initDomainConnections error", e);
    //        }
    //    }
    //
    //    private void initConnections(PoolingHttpClientConnectionManager manager, int numConnections, HttpHost httpHost) {
    //        HttpRoute httpRoute = new HttpRoute(httpHost);
    //        HttpContext httpContext = new BasicHttpContext();
    //        List<HttpClientConnection> connections = new ArrayList<>();
    //        IntStream.range(0, numConnections).forEach(i -> {
    //            try {
    //                ConnectionRequest connectionRequest = manager.requestConnection(httpRoute, null);
    //                HttpClientConnection connection = connectionRequest.get(3, TimeUnit.SECONDS);
    //                manager.connect(connection, httpRoute, 3000, httpContext);
    //                manager.routeComplete(connection, httpRoute, httpContext);
    //                connections.add(connection);
    //                log.info("connection status: {}", Objects.nonNull(connection) && connection.isOpen());
    //            } catch (Exception e) {
    //                log.error("initConnections {} error", httpHost, e);
    //            }
    //        });
    //        try {
    //            Optional.ofNullable(connections)
    //                    .orElse(Collections.emptyList())
    //                    .forEach(connection -> {
    //                        if (Objects.nonNull(connection) && connection.isOpen()) {
    //                            manager.releaseConnection(connection, null, 90, TimeUnit.MINUTES);
    //                        } else {
    //                            try {
    //                                connection.close();
    //                            } catch (Exception e) {
    //                                log.error("connection close error. {} ", httpHost, e);
    //                            }
    //                        }
    //                    });
    //        } catch (Exception e) {
    //            log.error("initConnections {} error", httpHost, e);
    //        }
    //    }

    public void warmUpConnections(CommonBaseApi commonBaseApi, int intConnections) {
        try {
            IntStream.range(0, intConnections)
                    .parallel()
                    .forEach(i -> {
                        try {
                            commonBaseApi.healthCheck();
                        } catch (Exception ignored) {
                            // 忽略所有异常
                        }
                    });
        } catch (Exception e) {
            log.error("warmUpConnections error", e);
        }
    }

    private void keepConnectionPool(PoolingHttpClientConnectionManager manager, CommonBaseApi api, int minIdle) {
        try {
            warmUpConnections(api, minIdle);
            //            int idleCount = manager.getTotalStats().getAvailable();
            //            // int leasedCount = manager.getTotalStats().getLeased();
            //            if (idleCount > 0) {
            //                log.info("keepConnectionPool idleCount: {}", idleCount);
            //                warmUpConnections(api, idleCount);
            //            }
            // 保活现有空闲连接
            //            if (idleCount > 0) {
            //                checkIdleConnections();
            //            }
        } catch (Exception e) {
            log.error("keepConnectionPool error", e);
        }
    }
}
