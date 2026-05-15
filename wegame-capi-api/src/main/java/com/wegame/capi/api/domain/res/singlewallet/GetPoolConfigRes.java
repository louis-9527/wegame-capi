package com.wegame.capi.api.domain.res.singlewallet;

import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

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
    @ToString(callSuper = true)
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
         * 奖池分类： 1：普通/ 2：星卡/ 100：全部 todo @bruce
         */
        private String poolCategory;
        /**
         * 当前档位
         */
        private String currentLevelNo;
        /**
         * 平台id   todo @bruce
         */
        private String pid;
        /**
         * 渠道id   todo @bruce
         */
        private String channelId;
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

        public boolean match(String userPid, String userChannelId, long betAmount) {
            return StringUtils.equals(userPid, pid)
                    && StringUtils.equals(userChannelId, channelId)
                    && betAmountInRange(betAmount);
        }

        private boolean betAmountInRange(long betAmount) {
            return Objects.nonNull(betAmountMin) && betAmountMin.multiply(BigDecimal.valueOf(1000)).longValue() <= betAmount
                    && Objects.nonNull(betAmountMax) && betAmount <= betAmountMax.multiply(BigDecimal.valueOf(1000)).longValue();
        }
    }
}

