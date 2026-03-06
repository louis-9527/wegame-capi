package com.wegame.capi.biz.config.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.wegame.base.domain.BizAlertMessage;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AppUtil;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.base.utils.LarkUtil;
import com.wegame.capi.biz.config.domain.ConfigValidate;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.util.Map;
import java.util.Objects;

/**
 * @author apple
 */
@Slf4j
public class NacosConfigRefresherUtil {

    private static final String bootId = "b2ff5555-4320-4c50-bfb5-5955cf1b6005";
    private static final Environment environment = AppUtil.getAppContext().getBean(Environment.class);

    public static final String MARIAMAKILING_JSON_ROOT_NAME = "mariamakiling";
    public static final String MALAKAS_JSON_ROOT_NAME = "malakas";

    private static final Map<String, Class<? extends ConfigValidate>> TO_CLASSTYPE_MAP = new ImmutableMap.Builder<String, Class<? extends ConfigValidate>>()
            .put("maria_makiling_config.json", MariaMakilingConfig.class)
            .put("malakas_config.json", MalakasConfig.class)
            .build();
    private static final Map<String, String> TO_JSON_ROOT_MAP = new ImmutableMap.Builder<String, String>()
            .put("maria_makiling_config.json", MARIAMAKILING_JSON_ROOT_NAME)
            .put("malakas_config.json", MALAKAS_JSON_ROOT_NAME)
            .build();

    public static boolean validate(String dataId, String groupId, String configInfo) {
        if (needValidate(dataId)) {
            try {
                String realConfigInfo = getRealConfigInfo(dataId, configInfo);
                AssertUtil.assertStrNotEmpty(realConfigInfo, ErrorCode.ERR_PARAM);
                Class classType = getClassType(dataId);
                AssertUtil.assertStrNotEmpty(realConfigInfo, ErrorCode.ERR_INTER);
                Object obj = FastJsonUtils.fromJSONString(realConfigInfo, classType);
                AssertUtil.assertTrue(obj instanceof ConfigValidate, ErrorCode.ERR_INTER);
                ConfigValidate config = (ConfigValidate) obj;
                config.validate();
                return true;
            } catch (Exception e) {
                log.error("config validate error", e);
                LarkUtil.sendTextMsg(environment.getProperty("lark.warningRobot"),
                        BizAlertMessage.builder()
                                .applicationName(environment.getProperty("spring.application.name"))
                                .env(environment.getProperty("spring.application.env"))
                                .body(String.format("%s, %s config validate error", dataId, groupId))
                                .build().buildMessage());
                return false;
            }
        }
        return true;
    }

    private static String getRealConfigInfo(String dataId, String configInfo) {
        String rootName = getJsonConfigRootName(dataId);
        if (StringUtils.isNotEmpty(rootName)) {
            JSONObject root = JSON.parseObject(configInfo);
            return Objects.nonNull(root) ? FastJsonUtils.toJSONString(root.getJSONObject(rootName)) : null;
        }
        return null;
    }

    private static Class getClassType(String dataId) {
        for (Map.Entry<String, Class<? extends ConfigValidate>> entry : TO_CLASSTYPE_MAP.entrySet()) {
            if (dataId.endsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static String getJsonConfigRootName(String dataId) {
        for (Map.Entry<String, String> entry : TO_JSON_ROOT_MAP.entrySet()) {
            if (dataId.endsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static boolean needValidate(String dataId) {
        return TO_CLASSTYPE_MAP.keySet().stream().filter(item -> dataId.contains(item)).findAny().isPresent();
    }
}
