package com.wegame.capi.biz.config.nacos;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * 配置读取器
 * 优先从本地读取，读取不到从nacos读取，并自动添加监听
 */
@Slf4j
@Component
public class ConfigReader {

    @Autowired
    private NacosConfigReader nacosConfigReader;

    /**
     * 优先从本地/xx.json读取文件，读取不到从nacos读取
     * 如果从nacos读取，会自动添加对该文件的监听
     *
     * @param configJsonName 配置文件名
     * @return InputStream
     */
    public InputStream getInputStream(String configJsonName) {
        InputStream inputStream = null;
        //        String path = "/" + configJsonName;

        //        // 优先从本地读取
        //        inputStream = getClass().getResourceAsStream(path);
        //        if (inputStream == null) {
        //            log.info("本地文件不存在: {}, 从 Nacos 读取", path);
                    inputStream = nacosConfigReader.getJsonAsInputStream(configJsonName);
        //        } else {
        //            log.info("从本地文件读取: {}", path);
        //        }

        return inputStream;
    }
}

