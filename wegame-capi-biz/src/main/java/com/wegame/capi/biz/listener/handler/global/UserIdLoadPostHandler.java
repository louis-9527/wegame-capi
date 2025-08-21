package com.wegame.capi.biz.listener.handler.global;

import com.wegame.base.exception.LockException;
import com.wegame.capi.biz.domain.user.UserDo;
import com.wegame.capi.biz.listener.handler.AbstractPostHandler;
import com.wegame.capi.biz.service.UserService;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.dao.mapper.domain.user.UserQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Order(0)
@Component
public class UserIdLoadPostHandler extends AbstractPostHandler {
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Override
    public void handle() {
        try {
            distributeLock.lockThenRun(LockNameUtil.getBuildUserIdCacheLockName(), 60, TimeUnit.SECONDS, () -> {
                //                redisTemplate.execute((RedisCallback<Void>) connection -> {
                //                    connection.flushAll();
                //                    log.info("delete all redis data");
                //                    return null;
                //                });
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
}
