package com.wegame.capi.api.domain.res.singlewallet;

import com.wegame.capi.client.enums.BetTypeEnum;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("获取奖池配置响应模型")
public class GetPoolConfigRes extends SingleWalletBaseRes {

    private static final int POOL_CATEGORY_NORMAL = 1;
    private static final int POOL_CATEGORY_BUY = 2;
    private static final int POOL_CATEGORY_ALL = 3;

    /**
     * agentCode
     */
    private String agentCode;
    /**
     * providerId
     */
    private String providerId;
    /**
     * 奖池配置列表
     */
    private List<BloodPoolConfig> pools;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ApiModel("血池配置")
    public static class BloodPoolConfig implements Serializable {
        /**
         * 奖池id
         */
        private String poolId;
        /**
         * 奖池名称
         */
        private String poolName;
        /**
         * 奖池分类： 1：普通/ 2：购买 / 100：全部
         */
        private int poolCategory;
        /**
         * 当前档位
         */
        private String currentLevelNo;
        ///**
        // * 平台id   todo @bruce
        // */
        //private String pid;
        ///**
        // * 渠道id   todo @bruce
        // */
        //private String channelId;
        /**
         * 支持的游戏
         */
        private List<String> gameCodes;
        /**
         * 最小投注金额
         */
        private BigDecimal betAmountMin;
        /**
         * 最大投注金额
         */
        private BigDecimal betAmountMax;
        /**
         * 单笔派奖上限
         */
        private Long singlePayoutLimit;
        /**
         * 最后变更时间
         */
        private Date lastChangeAt;

        public boolean match(long betAmount, BetTypeEnum betType) {
            return matchBetType(betType) && betAmountInRange(betAmount);
        }

        /**
         * 左开右闭
         */
        private boolean betAmountInRange(long betAmount) {
            return Objects.nonNull(betAmountMin) && betAmountMin.multiply(BigDecimal.valueOf(1000)).longValue() < betAmount
                    && Objects.nonNull(betAmountMax) && betAmount <= betAmountMax.multiply(BigDecimal.valueOf(1000)).longValue();
        }

        private boolean matchBetType(BetTypeEnum betType) {
            if (poolCategory == POOL_CATEGORY_ALL) {
                return true;
            } else {
                switch (betType) {
                    case NORMAL_BET:
                        return poolCategory == POOL_CATEGORY_NORMAL;
                    case BUY_BET:
                        return poolCategory == POOL_CATEGORY_BUY;
                }
                return false;
            }
        }
    }
}

