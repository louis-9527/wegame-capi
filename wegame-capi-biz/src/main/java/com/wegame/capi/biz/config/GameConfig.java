package com.wegame.capi.biz.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.config.nacos.NacosConfigReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author apple
 */
@Slf4j
@Configuration
public class GameConfig {

    public static final String MariaMakilingConfig_BeanName = "mariaMakilingConfig";
    public static final String MalakasConfig_BeanName = "malakasConfig";

    @Autowired
    NacosConfigReader nacosConfigReader;

    @Bean(MariaMakilingConfig_BeanName)
    MariaMakilingConfig mariaMakilingConfig() throws IOException {
        log.info("create MariaMakilingConfig bean...");
        String jsonConfig = nacosConfigReader.getJsonConfig("wegame-capi-local-maria_makiling_config.json");
        if (StringUtils.isEmpty(jsonConfig)) {
            throw new RuntimeException("read wegame-capi-local-maria_makiling_config.json file err！！！");
        } else {
            MariaMakilingConfig config = FastJsonUtils.fromJSONString(jsonConfig, MariaMakilingConfig.class);
            config.validate();
            log.info("create MariaMakilingConfig bean success");
            return config;
        }
    }

    @Bean(MalakasConfig_BeanName)
    MalakasConfig malakasConfig() throws IOException {
        log.info("create MalakasConfig bean...");
        String jsonConfig = nacosConfigReader.getJsonConfig("wegame-capi-local-malakas_config.json");
        if (StringUtils.isEmpty(jsonConfig)) {
            throw new RuntimeException("read wegame-capi-local-malakas_config.json err！！！");
        } else {
            MalakasConfig config = FastJsonUtils.fromJSONString(jsonConfig, MalakasConfig.class);
            config.validate();
            log.info("create MalakasConfig bean success");
            return config;
        }
    }

//    @Bean
//    MariaMakilingConfig mariaMakilingConfig() throws IOException {
//        String path = "/maria_makiling_config.json";
//        InputStream inputStream = getClass().getResourceAsStream(path);
//        if (inputStream == null) {
//            throw new RuntimeException("read maria_makiling_config.json file err！！！");
//        } else {
//            MariaMakilingConfig config = new ObjectMapper().readValue(inputStream, MariaMakilingConfig.class);
//            config.validate();
//            return config;
//        }
//    }

//    @Bean
//    MalakasConfig malakasConfig() throws IOException {
//        String path = "/malakas_config.json";
//        InputStream inputStream = getClass().getResourceAsStream(path);
//        if (inputStream == null) {
//            throw new RuntimeException("read malakas_config.json file err！！！");
//        } else {
//            MalakasConfig config = new ObjectMapper().readValue(inputStream, MalakasConfig.class);
//            config.validate();
//            return config;
//        }
//    }

    @Bean
    IbongAdarnaConfig ibongAdarnaConfig() throws IOException {
        String path = "/ibong_adarna_config.json";
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("read maria_makiling_config.json file err！！！");
        } else {
            IbongAdarnaConfig config = new ObjectMapper().readValue(inputStream, IbongAdarnaConfig.class);
            config.validate();
            return config;
        }
    }

}
