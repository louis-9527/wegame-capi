package com.wegame.capi.common.utils;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.GameTypeEnum;

public class LockNameUtil {

    // 定时任务锁
    private static final String BET_RECORD_STATUS_SCHEDULER_LOCK = "lock:bet_record_status_scheduler_lock:%s";
    private static final String BET_RECORD_ID_SCHEDULER_LOCK = "lock:bet_record_id_scheduler_lock:%s:%s";
    private static final String BET_RECORD_STATUS_ALERT_SCHEDULER_LOCK = "lock:bet_record_status_alert_scheduler_lock:%s";
    private static final String BET_LOCK = "lock:bet_lock_%s_%s";
    private static final String OPERATION_EVENT_SCHEDULER_LOCK = "lock:operation_event_scheduler_lock:%s";

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

    public static String getBetRecordStatusAlertSchedulerLockName(GameTypeEnum gameType) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        return String.format(BET_RECORD_STATUS_ALERT_SCHEDULER_LOCK, gameType.getName());
    }

    public static String getOperationEventSchedulerLockName(GameTypeEnum gameType) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        return String.format(OPERATION_EVENT_SCHEDULER_LOCK, gameType.getName());
    }
}
