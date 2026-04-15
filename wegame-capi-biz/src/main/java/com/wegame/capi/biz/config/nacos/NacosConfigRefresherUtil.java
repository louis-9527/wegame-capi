package com.wegame.capi.biz.config.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.base.utils.LarkUtil;
import com.wegame.capi.biz.config.domain.*;
import com.wegame.capi.biz.config.domain.BakunawaConfig;
import com.wegame.capi.biz.config.domain.ConfigValidate;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * @author apple
 */
@Slf4j
public class NacosConfigRefresherUtil {

    private static final String bootId = "b7a6477c-b829-41d4-acac-599c15aee2dc";

    public static final String MARIAMAKILING_JSON_ROOT_NAME = "mariamakiling";
    public static final String MALAKAS_JSON_ROOT_NAME = "malakas";
    public static final String BAKUNAWA_JSON_ROOT_NAME = "bakunawa";
    public static final String DIWATA_JSON_ROOT_NAME = "diwatamayari";
    public static final String IBONGADARNA_JSON_ROOT_NAME = "ibongadarna";
    public static final String LAMANG_JSON_ROOT_NAME = "lamang";

    private static final Map<String, Class<? extends ConfigValidate>> TO_CLASSTYPE_MAP = new ImmutableMap.Builder<String, Class<? extends ConfigValidate>>()
            .put("maria_makiling_config.json", MariaMakilingConfig.class)
            .put("malakas_config.json", MalakasConfig.class)
            .put("bakunawa_config.json", BakunawaConfig.class)
            .put("diwata_mayari_config.json", DiwataMayariConfig.class)
            .put("ibong_adarna_config.json", IbongAdarnaConfig.class)
            .put("lamang_config.json", LamangConfig.class)
            .build();
    private static final Map<String, String> TO_JSON_ROOT_MAP = new ImmutableMap.Builder<String, String>()
            .put("maria_makiling_config.json", MARIAMAKILING_JSON_ROOT_NAME)
            .put("malakas_config.json", MALAKAS_JSON_ROOT_NAME)
            .put("ibong_adarna_config.json", IBONGADARNA_JSON_ROOT_NAME)
            .put("bakunawa_config.json", BAKUNAWA_JSON_ROOT_NAME)
            .put("diwata_mayari_config.json", DIWATA_JSON_ROOT_NAME)
            .put("lamang_config.json", LAMANG_JSON_ROOT_NAME)
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
                log.info("config refresh suc, dataId: {}, groupId: {}", dataId, groupId);
                return true;
            } catch (Exception e) {
                log.error("config validate error, dataId: {}, groupId: {}, configInfo: {}", dataId, groupId, configInfo, e);
                LarkUtil.sendTextMsg(bootId, String.format("%s, %s config validate error. %s", dataId, groupId, e.getMessage()));
                return false;
            }
        } else {
            log.warn("config not in validate list, dataId: {}, groupId: {}", dataId, groupId);
        }
        return true;
    }

    private static String getRealConfigInfo(String dataId, String configInfo) {
        String rootName = getJsonConfigRootName(dataId);
        if (StringUtils.isNotEmpty(rootName)) {
            JSONObject root = JSON.parseObject(configInfo);
            return Objects.nonNull(root) ? FastJsonUtils.toJSONString(root.getJSONObject(rootName)) : null;
        } else {
            log.warn("not contains rootName, dataId: {}, rootName: {}", dataId, rootName);
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
        return TO_CLASSTYPE_MAP.keySet().stream().filter(item -> dataId.endsWith(item)).findAny().isPresent();
    }
}
