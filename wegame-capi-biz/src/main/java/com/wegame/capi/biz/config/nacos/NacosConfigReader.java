package com.wegame.capi.biz.config.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class NacosConfigReader {
    @Autowired
    NacosConfigManager configManager;
    @Autowired
    NacosConfigProperties nacosConfigProperties;

    /**
     * 从Nacos读取JSON配置并转换为InputStream
     *
     * @param dataId 配置ID（如：app-config.json）
     * @param group  配置分组（默认：DEFAULT_GROUP）
     * @return JSON配置的InputStream
     */
    public InputStream getJsonAsInputStream(String dataId, String group) {
        try {
            // 从Nacos获取JSON配置字符串
            String configString = configManager.getConfigService().getConfig(dataId,
                    group,
                    nacosConfigProperties.getTimeout());

            if (Strings.isEmpty(configString)) {
                throw new RuntimeException("Nacos中未找到配置: " + dataId + " (group: " + group + ")");
            }

            // 将字符串转换为InputStream（使用UTF-8编码）
            return new ByteArrayInputStream(configString.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            throw new RuntimeException("获取Nacos配置并转换为InputStream失败", e);
        }
    }

    // 简化方法：使用默认分组
    public InputStream getJsonAsInputStream(String dataId) {
        return getJsonAsInputStream(dataId, nacosConfigProperties.getGroup());
    }

}
