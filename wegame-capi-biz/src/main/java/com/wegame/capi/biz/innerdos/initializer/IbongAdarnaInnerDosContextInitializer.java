package com.wegame.capi.biz.innerdos.initializer;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.domain.metrics.GameMetricsDo;
import com.wegame.capi.biz.domain.metrics.UserPlayMetricsDo;
import com.wegame.capi.biz.innerdos.config.InnerDosCondition;
import com.wegame.capi.biz.innerdos.context.DefaultInnerDosContext;
import com.wegame.capi.biz.innerdos.context.InnerDosContext;
import com.wegame.capi.biz.service.metrics.GameMetricsDataService;
import com.wegame.capi.biz.service.metrics.UserPlayMetricsDataService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.innerdos.MetricsTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        MetricsTypeEnum metricsType = MetricsTypeEnum.valueOfGameType(GameTypeEnum.valueOfId(userInfo.getGameId()));
        GameMetricsDo gameMetricsDo = gameMetricsDataService.query(userInfo.getPId(), userInfo.getChannelId(), metricsType);
        UserPlayMetricsDo userPlayMetricsDo = userPlayMetricsDataService.query(userInfo.getPId(), userInfo.getPlayerId(), userInfo.getChannelId(), metricsType);

        DefaultInnerDosContext.DefaultTemplateRangeConfig templateRangeConfig = this.buildDefaultTemplateRangeConfig();
        InnerDosCondition innerDosCondition = this.buildInnerDosCondition();
        return DefaultInnerDosContext.builder()
                .defaultTemplateRangeConfig(templateRangeConfig)
                .innerDosCondition(innerDosCondition)
                .gameMetricsDo(gameMetricsDo)
                .userPlayMetricsDo(userPlayMetricsDo)
                .build();
    }

    private InnerDosCondition buildInnerDosCondition() {
        // 通过配置文件内容进行获取
        return this.ibongAdarnaConfig.getInnerDosConfig().getInnerDosCondition();
    }

    private DefaultInnerDosContext.DefaultTemplateRangeConfig buildDefaultTemplateRangeConfig() {
        return this.ibongAdarnaConfig.getInnerDosConfig().getTemplateRangeConfig();
    }
}
