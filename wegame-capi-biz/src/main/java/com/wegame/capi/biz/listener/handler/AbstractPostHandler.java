package com.wegame.capi.biz.listener.handler;

import com.google.common.collect.Lists;
import com.wegame.base.domain.BizAlertMessage;
import com.wegame.base.enums.EnvEnum;
import com.wegame.base.lock.DistributeLock;
import com.wegame.base.utils.LarkUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.infrastructure.config.properties.GameProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * 抽象后处理器
 */
@Slf4j
public abstract class AbstractPostHandler implements PostHandler {
    @Value("${spring.application.name}")
    public String appName;
    @Value("${spring.application.env}")
    public String env;
    @Value("${lark.warningRobot}")
    public String warningRobot;
    @Autowired
    public DistributeLock distributeLock;
    @Autowired
    public GameProperty gameProperty;

    public boolean supportGame(GameTypeEnum gameType) {
        return gameProperty.enabledGameTypes()
                .stream()
                .filter(item -> item == gameType)
                .findFirst()
                .orElse(null) != null;
    }


    public void warning(String warningMsg) {
        LarkUtil.sendTextMsg(warningRobot, BizAlertMessage.builder()
                .applicationName(appName)
                .env(env)
                .traceId(ThreadMdcUtil.getTraceId())
                .body(warningMsg)
                .build()
                .buildMessage());
    }

}
