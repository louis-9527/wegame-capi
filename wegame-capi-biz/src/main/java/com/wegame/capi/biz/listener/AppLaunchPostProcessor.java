package com.wegame.capi.biz.listener;

import com.wegame.base.exception.LockException;
import com.wegame.base.lock.DistributeLock;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.SingleWalletFreeSpinApi;
import com.wegame.capi.api.promotion.BagApi;
import com.wegame.capi.biz.domain.user.UserDo;
import com.wegame.capi.biz.service.RewardService;
import com.wegame.capi.biz.service.UserService;
import com.wegame.capi.biz.service.backend.GameMaintainConfigService;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.dao.mapper.domain.user.UserQuery;
import com.wegame.capi.infrastructure.config.properties.GameProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.defaultThreadPoolExecutor;

/**
 * app启动后处理器
 */
@Slf4j
@Component
public class AppLaunchPostProcessor {

    @Autowired
    GameProperty gameProperty;
    @Autowired
    RewardService rewardService;
    @Autowired
    GameMaintainConfigService gameMaintainConfigService;
    @Autowired
    DistributeLock distributeLock;
    @Autowired
    UserService userService;
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
    @Value("${api.game.singleDomainIntConnections:100}")
    int singleDomainIntConnections;
    @Value("${api.game.freeSpinDomain}")
    String freeSpinDomain;
    @Value("${api.game.freeSpinDomainIntConnections:100}")
    int freeSpinDomainIntConnections;
    @Value("${api.game.promotionDomain}")
    String promotionDomain;
    @Value("${api.game.promotionDomainIntConnections:100}")
    int promotionDomainIntConnections;
    @Autowired
    PoolingHttpClientConnectionManager walletConnectionManager;
    @Autowired
    PoolingHttpClientConnectionManager freeSpinConnectionManager;
    @Autowired
    PoolingHttpClientConnectionManager promotionConnectionManager;

    @Async(defaultThreadPoolExecutor)
    public void postProcess() {
        businessWarmUp();
        // todo 第一次上线后， 后续屏蔽该动作
        buildUserIdCache();
        initDomainConnections();
        healthCheck();
    }

    private void businessWarmUp() {
        try {
            gameProperty.enabledGameTypes().forEach(gameType -> {
                rewardService.getWithCache(gameType);
            });
            gameProperty.enabledGameTypes().forEach(gameType -> {
                gameMaintainConfigService.selectByGameId(gameType.getId());
            });
        } catch (Exception e) {
            log.error("businessWarmUp error", e);
        }
    }

    private void buildUserIdCache() {
        try {
            distributeLock.lockThenRun(LockNameUtil.getbuildUserIdCacheLockName(), 60, TimeUnit.SECONDS, () -> {
                try {
                    log.info("buildUserIdCache begin");
                    List<UserDo> userList;
                    UserQuery userQuery = UserQuery.builder()
                            .limit(1000)
                            .orderByClause(" order by id asc ")
                            .build();
                    Long maxId = 0L;
                    do {
                        if (Objects.nonNull(maxId)) {
                            userQuery.setGreaterThanId(maxId);
                        }
                        userList = userService.list(userQuery);
                        if (CollectionUtils.isNotEmpty(userList)) {
                            maxId = userList.get(userList.size() - 1).getId();
                            log.info("buildUserIdCache maxId: {}", maxId);
                        }
                        Optional.ofNullable(userList)
                                .orElse(Collections.emptyList())
                                .forEach(item -> {
                                    userService.asyncPutUserIdCache(item.getId(), item.getPlayerId(), item.getPId());
                                });
                    } while (CollectionUtils.isNotEmpty(userList));
                    log.info("buildUserIdCache end");
                } catch (LockException e) {
                    // do nothing
                } catch (Exception e) {
                    log.error("buildUserIdCache error", e);
                }
            }, null);
        } catch (Exception e) {
            log.error("buildUserIdCache error", e);
        }
    }

    private void initDomainConnections() {
        try {
            initConnections(walletConnectionManager, singleDomainIntConnections, HttpHost.create(singleDomain));
            initConnections(freeSpinConnectionManager, freeSpinDomainIntConnections, HttpHost.create(freeSpinDomain));
            initConnections(promotionConnectionManager, promotionDomainIntConnections, HttpHost.create(promotionDomain));
            //        String domain = "http://server1.makillingslot.net";
            //        HttpHost httpHost = HttpHost.create(domain);
            //        initConnections(walletConnectionManager, 10, httpHost);
        } catch (Exception e) {
            log.error("initDomainConnections error", e);
        }
    }

    private void initConnections(PoolingHttpClientConnectionManager manager, int numConnections, HttpHost httpHost) {
        HttpRoute httpRoute = new HttpRoute(httpHost);
        HttpContext httpContext = new BasicHttpContext();
        List<HttpClientConnection> connections = new ArrayList<>();
        IntStream.range(0, numConnections).forEach(i -> {
            try {
                ConnectionRequest connectionRequest = manager.requestConnection(httpRoute, null);
                HttpClientConnection connection = connectionRequest.get(10, TimeUnit.SECONDS);
                manager.connect(connection, httpRoute, 10000, httpContext);
                manager.routeComplete(connection, httpRoute, httpContext);
                connections.add(connection);
                log.info("connection status: {}", Objects.nonNull(connection) && connection.isOpen());
            } catch (Exception e) {
                log.error("initConnections {} error", httpHost, e);
            }
        });
        try {
            Optional.ofNullable(connections)
                    .orElse(Collections.emptyList())
                    .forEach(connection -> {
                        if (Objects.nonNull(connection) && connection.isOpen()) {
                            manager.releaseConnection(connection, null, 30, TimeUnit.MINUTES);
                        } else {
                            try {
                                connection.close();
                            } catch (Exception e) {
                                log.error("connection close error. {} ", httpHost, e);
                            }
                        }
                    });
        } catch (Exception e) {
            log.error("initConnections {} error", httpHost, e);
        }
    }

    private void healthCheck() {
        try {
            singleWalletApi.healthCheck();
            singleWalletFreeSpinApi.healthCheck();
            bagApi.healthCheck();
        } catch (Exception e) {
            log.error("healthCheck error", e);
        }
    }
}
