package com.wegame.capi.biz.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wegame.capi.biz.config.domain.BakunawaConfig;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.config.domain.LamangConfig;
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

    //@Bean 保留（example），勿删!!! 保留（example），勿删!!! 保留（example），勿删!!!
    //MariaMakilingConfig mariaMakilingConfig() throws Exception {
    //    String path = "/maria_makiling_config.json";
    //    String content = FileUtil.readClasspathFile(path);
    //    if (StringUtils.isEmpty(content)) {
    //        throw new RuntimeException("read maria_makiling_config.json file err！！！");
    //    } else {
    //        String configInfo = FileUtil.getRealJsonInfo(MARIAMAKILING_JSON_ROOT_NAME, content);
    //        MariaMakilingConfig config = FastJsonUtils.fromJSONString(configInfo, MariaMakilingConfig.class);
    //        config.validate();
    //        return config;
    //    }
    //}

//    @Bean
//    IbongAdarnaConfig ibongAdarnaConfig() throws IOException {
//        String path = "/ibong_adarna_config.json";
//        InputStream inputStream = getClass().getResourceAsStream(path);
//        if (inputStream == null) {
//            throw new RuntimeException("read maria_makiling_config.json file err！！！");
//        } else {
//            IbongAdarnaConfig config = new ObjectMapper().readValue(inputStream, IbongAdarnaConfig.class);
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

//    @Bean
//    LamangConfig lamangConfig() throws IOException {
//        String path = "/lamang_config.json";
//        InputStream inputStream = getClass().getResourceAsStream(path);
//        if (inputStream == null) {
//            throw new RuntimeException("read lamang_config.json file err！！！");
//        } else {
//            LamangConfig config = new ObjectMapper().readValue(inputStream, LamangConfig.class);
//            config.validate();
//            return config;
//        }
//    }

}
