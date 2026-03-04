package com.wegame.capi.biz.config.nacos;

import com.wegame.base.utils.FastJsonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Nacos配置
 */
@Slf4j
@Data
@SuperBuilder
@Component
@RefreshScope
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "player")
public class PlayerConfig implements InitializingBean {

    private String name;
    private Integer level;
    private List<String> skills;
    private Boolean vip;
    private String expireTime;


    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("skills: {}", FastJsonUtils.toJSONString(skills));
    }
}
