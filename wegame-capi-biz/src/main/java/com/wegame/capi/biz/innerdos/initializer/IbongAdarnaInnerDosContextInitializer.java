package com.wegame.capi.biz.innerdos.initializer;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.metrics.GameMetricsBuilder;
import com.wegame.capi.biz.builder.metrics.UserPlayMetricsBuilder;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.metrics.GameMetricsDo;
import com.wegame.capi.biz.domain.metrics.UserPlayMetricsDo;
import com.wegame.capi.biz.innerdos.config.GameRtpDosCondition;
import com.wegame.capi.biz.innerdos.config.InnerDosCondition;
import com.wegame.capi.biz.innerdos.context.DefaultInnerDosContext;
import com.wegame.capi.biz.innerdos.context.InnerDosContext;
import com.wegame.capi.biz.service.metrics.GameMetricsDataService;
import com.wegame.capi.biz.service.metrics.UserPlayMetricsDataService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.innerdos.GameInnerDosTypeEnum;
import com.wegame.capi.client.enums.innerdos.MetricsTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * ibong context初始化器
 */
@Slf4j
@Component
public class IbongAdarnaInnerDosContextInitializer implements InnerDosContextInitializer {
    @Autowired
    private IbongAdarnaConfig ibongAdarnaConfig;
    @Autowired
    private GameMetricsDataService gameMetricsDataService;
    @Autowired
    private UserPlayMetricsDataService userPlayMetricsDataService;

    @Override
    public GameTypeEnum gameType() {
        return GameTypeEnum.IBONG_ADARNA;
    }

    @Override
    public InnerDosContext init(Object... initParams) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        DefaultInnerDosContext.DefaultTemplateRangeConfig templateRangeConfig = this.buildDefaultTemplateRangeConfig();
        InnerDosCondition innerDosCondition = this.buildInnerDosCondition();
        return DefaultInnerDosContext.builder()
                .gamePlayDo((GamePlayDo) initParams[0])
                .defaultTemplateRangeConfig(templateRangeConfig)
                .innerDosCondition(innerDosCondition)
                .gameMetricsDo(this.queryGameMetricsDo(userInfo))
                .userPlayMetricsDo(this.queryUserPlayMetricsDo(userInfo))
                .build();
    }

    @Override
    public Map<GameInnerDosTypeEnum, GameRtpDosCondition> getGameRtpDosConditionMap() {
        return ibongAdarnaConfig.getInnerDosConfig().getGameRtpDosConditionMap();
    }

    private GameMetricsDo queryGameMetricsDo(UserInfo userInfo) {
        GameMetricsDo gameMetricsDo = gameMetricsDataService.query(userInfo.getPId(), userInfo.getChannelId(), MetricsTypeEnum.valueOfGameType(GameTypeEnum.valueOfId(userInfo.getGameId())));
        if (Objects.isNull(gameMetricsDo)) {
            // 没有返回默认：针对用户第一次访问兜底逻辑
            log.info("IbongAdarnaInnerDosContextInitializer no GameMetricsDo cache use default,userInfo:{}", JSON.toJSONString(userInfo));
            gameMetricsDo = GameMetricsBuilder.buildDefault(userInfo.getPId(), userInfo.getChannelId(), userInfo.getGameId());
        }
        return gameMetricsDo;
    }

    private UserPlayMetricsDo queryUserPlayMetricsDo(UserInfo userInfo) {
        UserPlayMetricsDo userPlayMetricsDo = userPlayMetricsDataService.query(userInfo.getPId(),
                userInfo.getPlayerId(),
                userInfo.getChannelId(),
                MetricsTypeEnum.valueOfGameType(GameTypeEnum.valueOfId(userInfo.getGameId())));

        if (Objects.isNull(userPlayMetricsDo)) {
            // 没有返回默认：针对用户第一次访问兜底逻辑
            log.info("IbongAdarnaInnerDosContextInitializer no UserPlayMetricsDo cache use default,userInfo:{}", JSON.toJSONString(userInfo));
            userPlayMetricsDo = UserPlayMetricsBuilder.buildDefault(userInfo);
        }
        return userPlayMetricsDo;
    }

    private InnerDosCondition buildInnerDosCondition() {
        // 通过配置文件内容进行获取
        return this.ibongAdarnaConfig.getInnerDosConfig().getInnerDosCondition();
    }

    private DefaultInnerDosContext.DefaultTemplateRangeConfig buildDefaultTemplateRangeConfig() {
        return this.ibongAdarnaConfig.getInnerDosConfig().getTemplateRangeConfig();
    }
}
