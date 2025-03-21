package com.wegame.capi.common.domain;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * 服务限流配置
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("服务限流配置")
public class ServerRateLimitConfigDo implements Serializable {
    private static final long serialVersionUID = -891587652158464L;

    private static final ServerRateLimitConfigCellDo DEFAULT_CELL_DO = ServerRateLimitConfigCellDo.builder()
            .onOpen(false)
            .permitsPerSecond(50D)
            .type("global")
            .build();

    /**
     * 全局
     */
    private ServerRateLimitConfigCellDo global;
    /**
     * 子配置
     */
    private List<ServerRateLimitConfigCellDo> children;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ApiModel("服务限流具体配置")
    public static class ServerRateLimitConfigCellDo {
        /**
         * 配置类型
         */
        private String type;
        /**
         * 限制流量
         * 多少/秒
         */
        private Double permitsPerSecond;
        /**
         * 是否生效
         * true:生效
         * false:不生效
         */
        private boolean onOpen;

    }

    //    public List<ServerRateLimitConfigCellDo> allConfigDoList() {
    //        if (){
    //
    //
    //
    //        }
    //
    //
    //        return Lists.newArrayList();
    //    }


}
