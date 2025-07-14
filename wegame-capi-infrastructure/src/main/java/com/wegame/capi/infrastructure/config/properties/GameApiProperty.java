package com.wegame.capi.infrastructure.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private List<PrivateInfo> privateInfoList;
    @Override
    public void afterPropertiesSet() throws Exception {
        privateInfoList = new LinkedList<>();
        privateInfoList.addAll(cp);
        privateInfoList.addAll(igo);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrivateInfo implements Serializable {
        private String platformId;
        private String channelId;
        private String privateKey;
        private String privateIv;

        //        public String getPlatformId() {
        //            return platformId;
        //        }
        //
        //        public void setPlatformId(String platformId) {
        //            this.platformId = platformId;
        //        }
        //
        //        public String getChannelId() {
        //            return channelId;
        //        }
        //
        //        public void setChannelId(String channelId) {
        //            this.channelId = channelId;
        //        }
        //
        //        public String getPrivateKey() {
        //            return privateKey;
        //        }
        //
        //        public void setPrivateKey(String privateKey) {
        //            this.privateKey = privateKey;
        //        }
        //
        //        public String getPrivateIv() {
        //            return privateIv;
        //        }
        //
        //        public void setPrivateIv(String privateIv) {
        //            this.privateIv = privateIv;
        //        }
    }
}
