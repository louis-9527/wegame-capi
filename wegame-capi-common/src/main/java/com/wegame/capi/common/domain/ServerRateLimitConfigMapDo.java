package com.wegame.capi.common.domain;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 服务限流map包装
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("服务限流配置")
public class ServerRateLimitConfigMapDo implements Serializable {
    private static final long serialVersionUID = -891587652158464L;

    private Map<String, ServerRateLimitConfigDo> configDoMap = new ConcurrentHashMap<>();

    private static final ServerRateLimitConfigDo.ServerRateLimitConfigCellDo DEFAULT_CELL_DO = ServerRateLimitConfigDo.ServerRateLimitConfigCellDo.builder()
            .onOpen(false)
            .permitsPerSecond(50D)
            .type("global")
            .build();

    public void clear() {
        configDoMap.clear();
    }

    public static ServerRateLimitConfigMapDo build(ServerRateLimitConfigDo rateLimitConfigDo) {
        if (Objects.isNull(rateLimitConfigDo)) {
            return new ServerRateLimitConfigMapDo();
        }


        return new ServerRateLimitConfigMapDo();
    }


}
