package com.wegame.capi.biz.listener;

import com.wegame.base.exception.LockException;
import com.wegame.base.lock.DistributeLock;
import com.wegame.capi.biz.domain.user.UserDo;
import com.wegame.capi.biz.service.RewardService;
import com.wegame.capi.biz.service.UserService;
import com.wegame.capi.biz.service.backend.GameMaintainConfigService;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.dao.mapper.domain.user.UserQuery;
import com.wegame.capi.infrastructure.config.properties.GameProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    RestTemplateWarmUpHelper restTemplateWarmUpHelper;

    @Async(defaultThreadPoolExecutor)
    public void postProcess() {
        businessWarmUp();
        // todo 第一次上线后， 后续屏蔽该动作
        buildUserIdCache();
        // warmUpConnections();
    }

    private void businessWarmUp() {
        try {
            gameProperty.enabledGameTypes().forEach(gameType -> rewardService.getWithCache(gameType));
            gameProperty.enabledGameTypes().forEach(gameType -> gameMaintainConfigService.selectByGameId(gameType.getId()));
        } catch (Exception e) {
            log.error("businessWarmUp error", e);
        }
    }

    private void buildUserIdCache() {
        try {
            distributeLock.lockThenRun(LockNameUtil.getBuildUserIdCacheLockName(), 60, TimeUnit.SECONDS, () -> {
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
                                .forEach(item -> userService.asyncPutUserIdCache(item.getId(), item.getPlayerId(), item.getPId()));
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

    private void warmUpConnections() {
        try {
            restTemplateWarmUpHelper.warmUpAllConnections();
        } catch (Exception e) {
            log.error("warmUpConnections error", e);
        }
    }
}
