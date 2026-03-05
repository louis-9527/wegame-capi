package com.wegame.capi.biz.config.nacos;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.base.utils.LarkUtil;
import com.wegame.capi.biz.config.domain.GameBizConfig;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author apple
 */
@Slf4j
public class NacosConfigRefresherUtil {

    private static final String bootId = "b2ff5555-4320-4c50-bfb5-5955cf1b6005";


    private static final Map<String, Class<? extends GameBizConfig>> map = new ImmutableMap.Builder<String, Class<? extends GameBizConfig>>()
            .put("maria_makiling_config.json", MariaMakilingConfig.class)
            .put("malakas_config.json", MalakasConfig.class)
//            .put("ibong_adarna_config.json", IbongAdarnaConfig.class)
            .build();

    public static boolean validate(String dataId, String groupId, String configInfo) {
        if (supportValidate(dataId)) {
            try {
                Class<? extends GameBizConfig> classType = getClassType(dataId);
                Object obj = FastJsonUtils.fromJSONString(configInfo, classType);
                GameBizConfig config = (GameBizConfig) obj;
                config.validate();
                return true;
            } catch (Exception e) {
                log.error("config validate error", e);
                LarkUtil.sendTextMsg(bootId, String.format("%s, %s config validate error", dataId, groupId));
                return false;
            }
        }
        return true;
    }

    private static Class<? extends GameBizConfig> getClassType(String dataId) {
        for (Map.Entry<String, Class<? extends GameBizConfig>> entry : map.entrySet()) {
            if (dataId.endsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static boolean supportValidate(String dataId) {
        return map.keySet().stream().filter(item -> dataId.contains(item)).findAny().isPresent();
    }
}
