package com.wegame.capi.biz.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import lombok.extern.slf4j.Slf4j;
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

    @Bean
    MariaMakilingConfig mariaMakilingConfig() throws IOException {
        String path = "/maria_makiling_config.json";
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("read maria_makiling_config.json file err！！！");
        } else {
            MariaMakilingConfig config = new ObjectMapper().readValue(inputStream, MariaMakilingConfig.class);
            config.validate();
            return config;
        }
    }

    @Bean
    MalakasConfig malakasConfig() throws IOException {
        String path = "/malakas_config.json";
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("read malakas_config.json file err！！！");
        } else {
            MalakasConfig config = new ObjectMapper().readValue(inputStream, MalakasConfig.class);
            config.validate();
            return config;
        }
    }
}
