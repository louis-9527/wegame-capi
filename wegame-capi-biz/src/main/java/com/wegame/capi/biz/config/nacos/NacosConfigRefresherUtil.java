package com.wegame.capi.biz.config.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.base.utils.LarkUtil;
import com.wegame.capi.biz.config.domain.*;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author apple
 */
@Slf4j
public class NacosConfigRefresherUtil {

    private static final String bootId = "b7a6477c-b829-41d4-acac-599c15aee2dc";

    public static final String MARIAMAKILING_JSON_ROOT_NAME = "mariamakiling";
    public static final String MALAKAS_JSON_ROOT_NAME = "malakas";
    public static final String IBONGADARNA_JSON_ROOT_NAME = "ibongadarna";
    public static final String LAMANG_JSON_ROOT_NAME = "lamang";
    public static final String BAKUNAWA_JSON_ROOT_NAME = "bakunawa";
    public static final String DIWATA_JSON_ROOT_NAME = "diwatamayari";
    public static final String KAPRE_JSON_ROOT_NAME = "kapre";
    public static final String MANGKUKULAM_JSON_ROOT_NAME = "mangkukulam";

    private static final Map<String, Class<? extends ConfigValidate>> TO_CLASSTYPE_MAP = new HashMap<>();
    private static final Map<String, String> TO_JSON_ROOT_MAP = new HashMap<>();
    private static final Map<GameTypeEnum, String> TO_GAME_TYPE_MAP = new HashMap<>();

    static {
        // TO_CLASSTYPE_MAP init
        TO_CLASSTYPE_MAP.put("maria_makiling_config.json", MariaMakilingConfig.class);
        TO_CLASSTYPE_MAP.put("malakas_config.json", MalakasConfig.class);
        TO_CLASSTYPE_MAP.put("ibong_adarna_config.json", IbongAdarnaConfig.class);
        TO_CLASSTYPE_MAP.put("bakunawa_config.json", BakunawaConfig.class);
        TO_CLASSTYPE_MAP.put("lamang_config.json", LamangConfig.class);
        TO_CLASSTYPE_MAP.put("diwata_mayari_config.json", DiwataMayariConfig.class);
        TO_CLASSTYPE_MAP.put("kapre_config.json", KapreConfig.class);
        TO_CLASSTYPE_MAP.put("mangkukulam_config.json", MangkukulamConfig.class);

        ////// 分割线 //////

        // TO_CLASSTYPE_MAP init
        TO_JSON_ROOT_MAP.put("maria_makiling_config.json", MARIAMAKILING_JSON_ROOT_NAME);
        TO_JSON_ROOT_MAP.put("malakas_config.json", MALAKAS_JSON_ROOT_NAME);
        TO_JSON_ROOT_MAP.put("ibong_adarna_config.json", IBONGADARNA_JSON_ROOT_NAME);
        TO_JSON_ROOT_MAP.put("bakunawa_config.json", BAKUNAWA_JSON_ROOT_NAME);
        TO_JSON_ROOT_MAP.put("diwata_mayari_config.json", DIWATA_JSON_ROOT_NAME);
        TO_JSON_ROOT_MAP.put("lamang_config.json", LAMANG_JSON_ROOT_NAME);
        TO_JSON_ROOT_MAP.put("kapre_config.json", KAPRE_JSON_ROOT_NAME);
        TO_JSON_ROOT_MAP.put("mangkukulam_config.json", MANGKUKULAM_JSON_ROOT_NAME);

        // TO_GAME_TYPE_MAP init
        TO_GAME_TYPE_MAP.put(GameTypeEnum.MARIA_MAKILING, "maria_makiling_config.json");
        TO_GAME_TYPE_MAP.put(GameTypeEnum.MALAKAS, "malakas_config.json");
        TO_GAME_TYPE_MAP.put(GameTypeEnum.IBONG_ADARNA, "ibong_adarna_config.json");
        TO_GAME_TYPE_MAP.put(GameTypeEnum.BAKUNAWA, "bakunawa_config.json");
        TO_GAME_TYPE_MAP.put(GameTypeEnum.LAM_ANG, "lamang_config.json");
        TO_GAME_TYPE_MAP.put(GameTypeEnum.DIWATA_MAYARI, "diwata_mayari_config.json");
        TO_GAME_TYPE_MAP.put(GameTypeEnum.KAPRE, "kapre_config.json");
        TO_GAME_TYPE_MAP.put(GameTypeEnum.MANGKUKULAM, "mangkukulam_config.json");
    }

    public static boolean validate(List<GameTypeEnum> gameTypeList, String dataId, String groupId, String configInfo) {
        if (needValidate(gameTypeList, dataId)) {
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

    private static boolean needValidate(List<GameTypeEnum> gameTypeList, String dataId) {
        List<String> values = TO_GAME_TYPE_MAP.entrySet()
                .stream()
                .filter(item -> gameTypeList.contains(item.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        return values.stream().filter(item -> dataId.endsWith(item)).findAny().isPresent();
        //return TO_CLASSTYPE_MAP.keySet().stream().filter(item -> dataId.endsWith(item)).findAny().isPresent();
    }

    public static boolean needAddListener(List<GameTypeEnum> gameTypeList, String dataId) {
        List<String> values = TO_GAME_TYPE_MAP.entrySet()
                .stream()
                .filter(item -> gameTypeList.contains(item.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        return values.stream().filter(item -> dataId.endsWith(item)).findAny().isPresent();
    }
}
