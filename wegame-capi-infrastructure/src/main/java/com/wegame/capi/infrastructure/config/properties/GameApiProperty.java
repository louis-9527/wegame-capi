package com.wegame.capi.infrastructure.config.properties;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.common.enums.DomainTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "api.game")
public class GameApiProperty implements InitializingBean, Serializable {

    private static final String unique_key_format = "%s_%s";

    private List<Config> multiPlatformList;

    /// 分割线 /////////////////////////////

    /**
     * key: pid_channelId
     * value: PrivateInfo
     */
    private Map<String, PrivateInfo> privateInfoMap;
    /**
     * pid -> domainType -> domain
     */
    private Map<String, Map<DomainTypeEnum, String>> domainInfoMap;
    /**
     * key: channelId (supportDos)
     * value: PrivateInfo
     */
    private Map<String, PrivateInfo> dosPrivateInfoMap;
    /**
     * key: channelId (supportDos)
     * value: Domain
     */
    private Map<String, String> dosDomainInfoMap;

    @Override
    public void afterPropertiesSet() throws Exception {
        fillPrivateInfoMap();
        fillDosInfo();
        fillDomainInfoMap();
        log.info("api game privateInfoMap: support pid_channelId {}", privateInfoMap.keySet());
        log.info("api game domainInfoMap:  {}", domainInfoMap);
        log.info("api game dosPrivateInfoMap: support channelId {}", dosPrivateInfoMap);
        log.info("api game dosPrivateInfoMap: support channelId {}", dosDomainInfoMap);
    }

    public boolean isInvokeSingle(String platformId) {
        AssertUtil.assertStrNotEmpty(platformId, ErrorCode.ERR_PARAM);
        Config config = multiPlatformList.stream().filter(item -> Objects.nonNull(item) && item.getPlatformIdList().contains(platformId)).findAny().orElse(null);
        if (Objects.isNull(config)) {
            // 默认调用，日志告警
            log.error("not support platformId: {} to judge orNot Invoke Single", platformId);
            return true;
        } else {
            return config.isInvokeSingle();
        }
    }

    public String getDomain(String platformId, DomainTypeEnum domainType) {
        AssertUtil.assertStrNotEmpty(platformId, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(domainType, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(domainType != DomainTypeEnum.OPERATION_DOMAIN, ErrorCode.ERR_INTER);
        Map<DomainTypeEnum, String> domainMap = domainInfoMap.get(platformId);
        AssertUtil.assertNotNull(domainMap, ErrorCode.ERR_INTER);
        String domain = domainMap.get(domainType);
        AssertUtil.assertStrNotEmpty(domain, ErrorCode.ERR_INTER);
        return domain;
    }

    public String getDosDomain(String channeId) {
        AssertUtil.assertStrNotEmpty(channeId, ErrorCode.ERR_INTER);
        String domain = dosDomainInfoMap.get(channeId);
        AssertUtil.assertStrNotEmpty(domain, ErrorCode.ERR_INTER);
        return domain;
    }

    public PrivateInfo getPrivateInfo(String platformId, String channelId) {
        return privateInfoMap.get(String.format(unique_key_format, platformId, channelId));
    }

    public PrivateInfo getDosPrivateInfo(String channelId) {
        return dosPrivateInfoMap.get(channelId);
    }

    public Set<String> supportDosChannelIdSet() {
        return dosPrivateInfoMap.keySet();
    }

    public List<PrivateInfo> supportDosPrivateInfoList() {
        return dosPrivateInfoMap.values().stream().collect(Collectors.toList());
    }

    private void fillPrivateInfoMap() {
        privateInfoMap = new HashMap<>();
        multiPlatformList.forEach(item -> {
            item.getPlatformMap().forEach((key, v) -> {
                privateInfoMap.putAll(v.stream().collect(Collectors.toMap(PrivateInfo::getUniqueKey, u -> u)));
            });
        });
    }

    private void fillDomainInfoMap() {
        domainInfoMap = new HashMap<>();
        multiPlatformList.forEach(item -> {
            Map<DomainTypeEnum, String> map = new HashMap<>();
            map.putIfAbsent(DomainTypeEnum.SINGLE_DOMAIN, item.getSingleDomain());
            map.putIfAbsent(DomainTypeEnum.SINGLE_POOL_CONFIG_DOMAIN, item.getSinglePoolConfigDomain());
            map.putIfAbsent(DomainTypeEnum.FREE_SPIN_DOMAIN, item.getFreeSpinDomain());
            map.putIfAbsent(DomainTypeEnum.PROMOTION_DOMAIN, item.getPromotionDomain());
            map.putIfAbsent(DomainTypeEnum.OPERATION_DOMAIN, item.getOperationDomain());
            item.getPlatformIdList().forEach(pid -> {
                domainInfoMap.putIfAbsent(pid, map);
            });
        });
    }

    private void fillDosInfo() {
        dosPrivateInfoMap = new HashMap<>();
        multiPlatformList.forEach(item -> {
            item.getPlatformMap().forEach((platformName, privateInfoList) -> {
                dosPrivateInfoMap.putAll(privateInfoList.stream()
                        .filter(PrivateInfo::isDosSupport)
                        .collect(Collectors.toMap(PrivateInfo::getChannelId, u -> u)));
            });
        });

        dosDomainInfoMap = new HashMap<>();
        multiPlatformList.forEach(item -> {
            item.getPlatformMap().forEach((platformName, privateInfoList) -> {
                dosDomainInfoMap.putAll(privateInfoList.stream()
                        .filter(PrivateInfo::isDosSupport)
                        .collect(Collectors.toMap(PrivateInfo::getChannelId, u -> item.getOperationDomain())));
            });
        });
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config implements Serializable {
        private List<String> platformIdList;
        private boolean invokeSingle;
        private String singleDomain;
        private String singlePoolConfigDomain;
        private String freeSpinDomain;
        private String promotionDomain;
        private String operationDomain;
        private Map<String, List<PrivateInfo>> platformMap;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrivateInfo implements Serializable {
        /**
         * 平台id
         */
        private String platformId;
        /**
         * 渠道id
         */
        private String channelId;
        /**
         * privateKey
         */
        private String privateKey;
        /**
         * privateIv
         */
        private String privateIv;
        /**
         * 是否支持干预事件: 默认不支持
         */
        private boolean dosSupport = false;
        /**
         * 用于上报干预结果
         */
        private String dosPublicKey;
        /**
         * 用于上报干预结果
         */
        private String dosPrivateKey;
        /**
         * 用于上报干预结果
         */
        private String dosPassword;

        private String getUniqueKey() {
            return String.format(unique_key_format, platformId, channelId);
        }
    }
}
