package com.wegame.capi.biz.config.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NacosConfigReader {
    @Autowired
    NacosConfigManager configManager;
    @Autowired
    NacosConfigProperties nacosConfigProperties;

    public String getJsonConfig(String dataId) {
        try {
            String configString = configManager.getConfigService().getConfig(dataId,
                    nacosConfigProperties.getGroup(),
                    nacosConfigProperties.getTimeout());
            if (Strings.isEmpty(configString)) {
                throw new RuntimeException("not fond nacos config: " + dataId + " (group: " + nacosConfigProperties.getGroup() + ")");
            }
            return configString;
        } catch (Exception e) {
            log.error("getJsonConfig error", e);
            throw new RuntimeException("getJsonConfig error", e);
        }
    }
}
