package com.wegame.capi.infrastructure.config.properties;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.common.enums.DomainTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
    //    private String singleDomain;
    //    private boolean invokeSingle;
    //    private String freeSpinDomain;
    //    private String promotionDomain;
    //    private String operationDomain;
    //    private List<PrivateInfo> cp;
    //    private List<PrivateInfo> igo;

    /**
     * 内部平台
     */
    private Internal internal;
    /**
     * 外部平台
     */
    private External external;
    /**
     * privateInfoList
     */
    private List<PrivateInfo> privateInfoList;
    /**
     * key: pid_channelId
     * value: PrivateInfo
     */
    private Map<String, PrivateInfo> privateInfoMap;
    /**
     * pid -> domainType -> domain
     */
    private Map<String, Map<DomainTypeEnum, String>> domainInfoMap;


    @Override
    public void afterPropertiesSet() throws Exception {
        // fill privateInfoList
        privateInfoList = new LinkedList<>();
        if (Objects.nonNull(internal)) {
            if (CollectionUtils.isNotEmpty(internal.getCp())) {
                privateInfoList.addAll(internal.getCp());
            }
            if (CollectionUtils.isNotEmpty(internal.getIgo())) {
                privateInfoList.addAll(internal.getIgo());
            }
        }
        if (Objects.nonNull(external)) {
            if (CollectionUtils.isNotEmpty(external.getBp())) {
                privateInfoList.addAll(external.getBp());
            }
        }
        // fill privateInfoMap
        privateInfoMap = new HashMap<>();
        if (Objects.nonNull(internal)) {
            if (CollectionUtils.isNotEmpty(internal.getCp())) {
                privateInfoMap.putAll(internal.getCp()
                        .stream()
                        .collect(Collectors.toMap(PrivateInfo::getUniqueKey, u -> u)));
            }
            if (CollectionUtils.isNotEmpty(internal.getIgo())) {
                privateInfoMap.putAll(internal.getIgo()
                        .stream()
                        .collect(Collectors.toMap(PrivateInfo::getUniqueKey, u -> u)));
            }
        }
        if (Objects.nonNull(external)) {
            if (CollectionUtils.isNotEmpty(external.getBp())) {
                privateInfoMap.putAll(external.getBp()
                        .stream()
                        .collect(Collectors.toMap(PrivateInfo::getUniqueKey, u -> u)));
            }
        }
        // fill domainInfoMap
        domainInfoMap = new HashMap<>();
        if (Objects.nonNull(internal)) {
            Map<DomainTypeEnum, String> internalMap = new HashMap<>();
            internalMap.putIfAbsent(DomainTypeEnum.SINGLE_DOMAIN, internal.getSingleDomain());
            internalMap.putIfAbsent(DomainTypeEnum.FREE_SPIN_DOMAIN, internal.getFreeSpinDomain());
            internalMap.putIfAbsent(DomainTypeEnum.PROMOTION_DOMAIN, internal.getPromotionDomain());
            internalMap.putIfAbsent(DomainTypeEnum.OPERATION_DOMAIN, internal.getOperationDomain());
            internal.getPlatformIdList().forEach(item -> {
                domainInfoMap.putIfAbsent(item, internalMap);
            });
        }
        if (Objects.nonNull(external)) {
            Map<DomainTypeEnum, String> externalMap = new HashMap<>();
            externalMap.putIfAbsent(DomainTypeEnum.SINGLE_DOMAIN, external.getSingleDomain());
            externalMap.putIfAbsent(DomainTypeEnum.FREE_SPIN_DOMAIN, external.getFreeSpinDomain());
            externalMap.putIfAbsent(DomainTypeEnum.PROMOTION_DOMAIN, external.getPromotionDomain());
            externalMap.putIfAbsent(DomainTypeEnum.OPERATION_DOMAIN, external.getOperationDomain());
            external.getPlatformIdList().forEach(item -> {
                domainInfoMap.putIfAbsent(item, externalMap);
            });
        }
    }

    public boolean isInvokeSingle(String platformId) {
        AssertUtil.assertStrNotEmpty(platformId, ErrorCode.ERR_PARAM);
        if (Objects.nonNull(internal) && internal.getPlatformIdList().contains(platformId)) {
            return internal.isInvokeSingle();
        } else if (Objects.nonNull(external) && external.getPlatformIdList().contains(platformId)) {
            return external.isInvokeSingle();
        } else {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
    }

    public String getDomain(String platformId, DomainTypeEnum domainType) {
        AssertUtil.assertStrNotEmpty(platformId, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(domainType, ErrorCode.ERR_INTER);
        Map<DomainTypeEnum, String> domainMap = domainInfoMap.get(platformId);
        AssertUtil.assertNotNull(domainMap, ErrorCode.ERR_INTER);
        String domain = domainMap.get(domainType);
        AssertUtil.assertStrNotEmpty(domain, ErrorCode.ERR_INTER);
        return domain;
    }

    public PrivateInfo getPrivateInfo(String platformId, String channelId) {
        return privateInfoMap.get(String.format(unique_key_format, platformId, channelId));
        //        return privateInfoList.stream()
        //                .filter(privateInfo -> StringUtils.equals(privateInfo.getPlatformId(), platformId) && StringUtils.equals(privateInfo.getChannelId(), channelId))
        //                .findFirst()
        //                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_PARAM));
    }

    /**
     * todo 确认dos逻辑
     * @return
     */
    public PrivateInfo getPrivateInfo(String channelId) {
        return privateInfoList.stream()
                .filter(privateInfo -> StringUtils.equals(privateInfo.getChannelId(), channelId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_PARAM));
    }

    /**
     * todo 确认dos逻辑
     * @return
     */
    public Set<String> getChannelIdAll() {
        return privateInfoList.stream()
                .map(PrivateInfo::getChannelId)
                .collect(Collectors.toSet());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Common implements Serializable {
        private List<String> platformIdList;
        private boolean invokeSingle;
        private String singleDomain;
        private String freeSpinDomain;
        private String promotionDomain;
        private String operationDomain;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Internal extends Common implements Serializable {
        private static final long serialVersionUID = 372342439L;
        private List<PrivateInfo> cp;
        private List<PrivateInfo> igo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class External extends Common implements Serializable {
        private static final long serialVersionUID = 37234243943254352L;
        private List<PrivateInfo> bp;
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
         * 用于上报干预结果
         */
        private String privateKey;
        /**
         * 用于上报干预结果
         */
        private String privateIv;
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
