package com.wegame.capi.biz.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wegame.capi.biz.config.domain.*;
import com.wegame.capi.biz.config.domain.BakunawaConfig;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.config.domain.DiwataMayariConfig;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.config.domain.*;
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
    IbongAdarnaConfig ibongAdarnaConfig() throws IOException {
        String path = "/ibong_adarna_config.json";
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("read ibong_adarna_config.json file err！！！");
        } else {
            IbongAdarnaConfig config = new ObjectMapper().readValue(inputStream, IbongAdarnaConfig.class);
            config.validate();
            return config;
        }
    }

    @Bean
    LamangConfig lamangConfig() throws IOException {
        String path = "/lamang_config.json";
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("read lamang_config.json file err！！！");
        } else {
            LamangConfig config = new ObjectMapper().readValue(inputStream, LamangConfig.class);
            config.validate();
            return config;
        }
    }

//    @Bean
//    DiwataMayariConfig diwataMayariConfig() throws IOException {
//        String path = "/diwata_mayari_config.json";
//        InputStream inputStream = getClass().getResourceAsStream(path);
//        if (inputStream == null) {
//            throw new RuntimeException("read diwata_mayari_config.json file err！！！");
//        } else {
//            DiwataMayariConfig config = new ObjectMapper().readValue(inputStream, DiwataMayariConfig.class);
//            config.validate();
//            return config;
//        }
//    }

    @Bean
    BakunawaConfig bakunawaConfig() throws IOException {
        String path = "/bakunawa.json";
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("read bakunawa.json file err！！！");
        } else {
            BakunawaConfig config = new ObjectMapper().readValue(inputStream, BakunawaConfig.class);
            config.validate();
            return config;
        }
    }

    @Bean
    LamangConfig lamangConfig() throws IOException {
        String path = "/lamang_config.json";
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("read lamang_config.json file err！！！");
        } else {
            LamangConfig config = new ObjectMapper().readValue(inputStream, LamangConfig.class);
            config.validate();
            return config;
        }
    }

    @Bean
    DiwataMayariConfig diwataMayariConfig() throws IOException {
        String path = "/diwata_mayari_config.json";
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("read diwata_mayari_config.json file err！！！");
        } else {
            DiwataMayariConfig config = new ObjectMapper().readValue(inputStream, DiwataMayariConfig.class);
            config.validate();
            return config;
        }
    }

    @Bean
    KapreConfig kapreConfig() throws IOException {
        String path = "/kapre_config.json";
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("read kapre_config.json file err！！！");
        } else {
            KapreConfig config = new ObjectMapper().readValue(inputStream, KapreConfig.class);
            config.validate();
            return config;
        }
    }

    @Bean
    MangkukulamConfig mangkukulamConfig() throws IOException {
        String path = "/mangkukulam.json";
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("read mangkukulam.json file err！！！");
        } else {
            MangkukulamConfig config = new ObjectMapper().readValue(inputStream, MangkukulamConfig.class);
            config.validate();
            return config;
        }
    }

    @Bean
    SirenaConfig sirenaConfig() throws IOException {
        String path = "/sirena_config.json";
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("read sirena_config.json file err！！！");
        } else {
            SirenaConfig config = new ObjectMapper().readValue(inputStream, SirenaConfig.class);
            config.validate();
            return config;
        }
    }

    @Bean
    AtlantisConfig atlantisConfig() throws IOException {
        String path = "/atlantis_config.json";
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("read atlantis_config.json file err！！！");
        } else {
            AtlantisConfig config = new ObjectMapper().readValue(inputStream, AtlantisConfig.class);
            config.validate();
            return config;
        }
    }
}
