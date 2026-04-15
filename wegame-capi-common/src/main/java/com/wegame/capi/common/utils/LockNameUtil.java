package com.wegame.capi.common.utils;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.TemplateTypeEnum;

public class LockNameUtil {

    // 定时任务锁
    private static final String BET_RECORD_STATUS_SCHEDULER_LOCK = "lock:bet_record_status_scheduler_lock:%s";
    private static final String BET_RECORD_STATUS_ALERT_SCHEDULER_LOCK = "lock:bet_record_status_alert_scheduler_lock:%s";
    private static final String METRICS_LOCK = "lock:metrics_lock:%s";
    private static final String BET_LOCK = "lock:bet_lock:%s";
    private static final String OPERATION_EVENT_SCHEDULER_LOCK = "lock:operation_event_scheduler_lock:%s";

    private static final String OPERATION_EVENT_REPORT_LOCK = "lock:operation_event_report_lock:%s_%s";
    private static final String STAR_CARD_REPORT_SCHEDULER_LOCK = "lock:star_card_report_scheduler_lock:%s";

    private static final String MAINTAIN_CONFIG_UPDATE_LOCK = "lock:maintain_config_update_scheduler_lock:%s";

    private static final String BUILD_USER_ID_CACHE_LOCK = "lock:build_user_id_cache_lock";
    // BetRecordTemplatePreLoad
    private static final String LOAD_BET_RECORD_TEMPLATE_CACHE_LOCK = "lock:load_bet_record_template_cache_lock:%s";


    private LockNameUtil() {
    }

    /// ====== 分割线 定时任务锁 ====== ///
    public static final String getBetLockName(String playerName) {
        return String.format(BET_LOCK, playerName);
    }

    public static String getMetricsLockName(GameTypeEnum gameType) {
        return String.format(METRICS_LOCK, gameType.getName());
    }

    public static String getBetRecordStatusSchedulerLockName(GameTypeEnum gameType) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        return String.format(BET_RECORD_STATUS_SCHEDULER_LOCK, gameType.getName());
    }

    public static String getBetRecordStatusAlertSchedulerLockName(GameTypeEnum gameType) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        return String.format(BET_RECORD_STATUS_ALERT_SCHEDULER_LOCK, gameType.getName());
    }

    public static String getOperationEventSchedulerLockName(GameTypeEnum gameType) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        return String.format(OPERATION_EVENT_SCHEDULER_LOCK, gameType.getName());
    }

    public static String getOperationEventReportLockName(GameTypeEnum gameType, Long betRecordId) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        return String.format(OPERATION_EVENT_REPORT_LOCK, gameType.getName(), betRecordId);
    }


    public static String getStarCardReportSchedulerLockName(GameTypeEnum gameType) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        return String.format(STAR_CARD_REPORT_SCHEDULER_LOCK, gameType.getName());
    }

    public static String getMaintainConfigUpdateLock(GameTypeEnum gameType) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        return String.format(MAINTAIN_CONFIG_UPDATE_LOCK, gameType.getName());
    }

    public static String getBuildUserIdCacheLockName() {
        return BUILD_USER_ID_CACHE_LOCK;
    }

    public static String getBetRecordTemplatePreLoadLockName(TemplateTypeEnum templateType) {
        return String.format(LOAD_BET_RECORD_TEMPLATE_CACHE_LOCK, templateType.name());
    }
}
