package com.wegame.capi.common.utils;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.GameTypeEnum;

public class LockNameUtil {

    // 定时任务锁
    private static final String BET_RECORD_STATUS_SCHEDULER_LOCK = "lock:bet_record_status_scheduler_lock:%s";
    private static final String BET_RECORD_ID_SCHEDULER_LOCK = "lock:bet_record_id_scheduler_lock:%s:%s";
    private static final String DYNAMIC_OPERATION_BET_LOCK = "lock:dynamic_operation_bet_lock:%s:%s";

    private static final String BET_LOCK = "lock:bet_lock_%s_%s";

    private static final String DYNAMIC_OPERATION_STATUS_SCHEDULER_LOCK = "lock:dynamic_operation_status_scheduler_lock:%s";


    private LockNameUtil() {
    }

    /// ====== 分割线 定时任务锁 ====== ///
    public static String getBetRecordStatusSchedulerLockName(GameTypeEnum gameType) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        return String.format(BET_RECORD_STATUS_SCHEDULER_LOCK, gameType.getName());
    }

    public static String getBetRecordIdSchedulerLockName(GameTypeEnum gameType, Long betRecordId) {
        AssertUtil.assertNotNull(betRecordId, ErrorCode.ERR_PARAM, "betRecordId is null");
        return String.format(BET_RECORD_ID_SCHEDULER_LOCK, gameType.getName(), betRecordId);
    }

    public static String getDynamicOperationBetLockName(String playerId, String gameId) {
        AssertUtil.assertStrNotEmpty(playerId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(gameId, ErrorCode.ERR_PARAM);
        return String.format(DYNAMIC_OPERATION_BET_LOCK, playerId, gameId);
    }

    public static String getDynamicOperationSchedulerLockName(GameTypeEnum gameType) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        return String.format(DYNAMIC_OPERATION_STATUS_SCHEDULER_LOCK, gameType.getName());
    }

}
