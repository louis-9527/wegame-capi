package com.wegame.capi.common.constants;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BizInterfereTypeEnum;

/**
 * 业务干预模块常量
 */
public class BizInterfereConstants {
    /**
     * 业务干预hash redis key format
     * eg:wegame:capi:bizInterfere:{gameId}:{bizInterfereSubType}->wegame:capi:bizInterfere:zhumeng006:1
     */
    public static String BIZ_INTERFERE_REDIS_HASH_KEY_FORMAT = "wegame:capi:bizInterfere:%s:%s";

    public static String buildBizInterfereRedisHashKey(GameTypeEnum gameType, BizInterfereTypeEnum bizInterfereType) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        AssertUtil.assertNotNull(bizInterfereType, ErrorCode.ERR_PARAM, "bizInterfereType is null");
        return String.format(BIZ_INTERFERE_REDIS_HASH_KEY_FORMAT, gameType.getId(), bizInterfereType.getCode());
    }


}
