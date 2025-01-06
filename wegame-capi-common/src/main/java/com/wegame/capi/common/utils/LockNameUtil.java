package com.wegame.capi.common.utils;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;

public class LockNameUtil {

    // 定时任务锁
    private static final String BET_RECORD_PAY_OUT_SCHEDULER_LOCK = "lock:bet_record_pay_out_scheduler_lock";

    private static final String BET_LOCK = "lock:bet_lock_%s_%s";


    private LockNameUtil() {
    }

    /// ====== 分割线 定时任务锁 ====== ///

    public static String getBetRecordPayOutStatusSchedulerLockName() {
        return BET_RECORD_PAY_OUT_SCHEDULER_LOCK;
    }

    public static String getBetLockName(String playerId, String gameId) {
        AssertUtil.assertStrNotEmpty(playerId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(playerId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(gameId, ErrorCode.ERR_PARAM);
        return String.format(BET_LOCK, playerId, gameId);
    }

}
