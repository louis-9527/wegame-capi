package com.wegame.capi.biz.config.es;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.List;

/**
 * "${" +
 * // XXX_PROP + "} || ${" +  // 新增游戏加该配置
 * DIWATA_MAYARI_PROP + "} || ${" +
 * BAKUNAWA_PROP + "} || ${" +
 * LAMANG_PROP + "} || ${" +
 * IBONG_ADARNA_PROP + "} || ${" +
 * MALAKAS_PROP + "} || ${" +
 * MANGKUKULAM_PROP + "} || ${" +
 * MARIA_MAKILING_PROP
 * + "}"
 *
 * @see EsRestClientConfig
 */
@Slf4j
public class EsConfigEnableCondition implements Condition {

    private static final List<String> ES_CONFIG_CONDITIONAL_PROPERTIES = Lists.newArrayList(Constants.DIWATA_MAYARI_PROP,
            Constants.BAKUNAWA_PROP,
            Constants.LAMANG_PROP,
            Constants.IBONG_ADARNA_PROP,
            Constants.MALAKAS_PROP,
            Constants.MANGKUKULAM_PROP,
            Constants.MARIA_MAKILING_PROP,
            Constants.KAPRE_PROP,
            Constants.SIRENA_PROP);

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        AssertUtil.assertTrue(CollectionUtil.isNotEmpty(ES_CONFIG_CONDITIONAL_PROPERTIES), ErrorCode.ERR_PARAM, "es config conditional is empty");
        Environment environment = context.getEnvironment();
        for (String gameEnableProperty : ES_CONFIG_CONDITIONAL_PROPERTIES) {
            if (environment.getProperty(gameEnableProperty, Boolean.class, false)) {
                return true;
            }
        }
        return false;
    }
}
