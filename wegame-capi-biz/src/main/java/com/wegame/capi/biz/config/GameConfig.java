package com.wegame.capi.biz.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;


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

}
