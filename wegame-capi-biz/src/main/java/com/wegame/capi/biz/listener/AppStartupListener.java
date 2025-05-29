package com.wegame.capi.biz.listener;

import com.wegame.base.exception.LockException;
import com.wegame.base.lock.DistributeLock;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.api.SingleWalletFreeSpinApi;
import com.wegame.capi.api.promotion.BagApi;
import com.wegame.capi.biz.domain.user.UserDo;
import com.wegame.capi.biz.service.LoginService;
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
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.wegame.capi.infrastructure.config.rest.WalletRestTemplateConfig.WALLET_CONNECTION_MANAGER_BEAN;

@Slf4j
@Component
public class AppStartupListener implements ApplicationListener<ContextRefreshedEvent> {

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
    // helthCheck
    @Autowired
    SingleWalletApi singleWalletApi;
    @Autowired
    SingleWalletFreeSpinApi singleWalletFreeSpinApi;
    @Autowired
    BagApi bagApi;
    // init Connections
    @Value("${api.game.singleDomain}")
    public String singleDomain;
    @Value("${api.game.singleDomainIntConnections:150}")
    public int singleDomainIntConnections;
    @Value("${api.game.freeSpinDomain}")
    public String freeSpinDomain;
    @Value("${api.game.freeSpinDomainIntConnections:150}")
    public int freeSpinDomainIntConnections;
    @Value("${api.game.promotionDomain}")
    public String promotionDomain;
    @Value("${api.game.promotionDomainIntConnections:150}")
    public int promotionDomainIntConnections;
    @Autowired
    PoolingHttpClientConnectionManager walletConnectionManager;
    @Autowired
    PoolingHttpClientConnectionManager promotionConnectionManager;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 执行业务预热操作
        log.info("Application started. Performing business warm-up start...");
        businessWarmUp();
        buildUserIdCache();
        initDomainConnections();
        healthCheck();
        log.info("Application started. Performing business warm-up start...");
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
//        initConnections(walletConnectionManager, singleDomainIntConnections, HttpHost.create(singleDomain));
//        initConnections(walletConnectionManager, freeSpinDomainIntConnections, HttpHost.create(freeSpinDomain));
//        initConnections(promotionConnectionManager, promotionDomainIntConnections, HttpHost.create(promotionDomain));
        String domain = "http://server1.makillingslot.net";
        HttpHost httpHost = HttpHost.create(domain);
        initConnections(walletConnectionManager, 30, httpHost);
    }

    private void initConnections(PoolingHttpClientConnectionManager manager, int numConnections, HttpHost httpHost) {
        HttpRoute httpRoute = new HttpRoute(httpHost);
        HttpContext httpContext = new BasicHttpContext();
        IntStream.range(0, numConnections).forEach(i -> {
            try {
                ConnectionRequest connectionRequest = manager.requestConnection(httpRoute, null);
                HttpClientConnection connection = connectionRequest.get(10, TimeUnit.SECONDS);
                manager.connect(connection, httpRoute, 1000, httpContext);
            } catch (Exception e) {
                log.error("initConnections {}, [{}] error", httpHost, i, e);
            }
        });
    }

    //    private void initConnectionPool() {
    //        log.info("begin initConnectionPool");
    //        singleWalletApi.initConnections();
    //        singleWalletFreeSpinApi.initConnections();
    //        bagApi.initConnections();
    //        log.info("end initConnectionPool");
    //    }

    private void healthCheck() {
        bagApi.healthCheck();
        singleWalletApi.healthCheck();
        singleWalletFreeSpinApi.healthCheck();
    }
}
