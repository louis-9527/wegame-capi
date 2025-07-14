package com.wegame.capi.infrastructure.config.properties;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "api.game")
public class GameApiProperty implements InitializingBean, Serializable {

    private String singleDomain;
    private boolean invokeSingle;
    private String freeSpinDomain;
    private String promotionDomain;
    private String operationDomain;
    private List<PrivateInfo> cp;
    private List<PrivateInfo> igo;

    //
    private List<PrivateInfo> privateInfoList;

    @Override
    public void afterPropertiesSet() throws Exception {
        privateInfoList = new LinkedList<>();
        privateInfoList.addAll(cp);
        privateInfoList.addAll(igo);
    }

    public PrivateInfo getPrivateInfo(String platformId, String channelId) {
        return privateInfoList.stream()
                .filter(privateInfo -> StringUtils.equals(privateInfo.getPlatformId(), platformId) && StringUtils.equals(privateInfo.getChannelId(), channelId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ERR_PARAM));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrivateInfo implements Serializable {
        private String platformId;
        private String channelId;
        private String privateKey;
        private String privateIv;
    }
}
