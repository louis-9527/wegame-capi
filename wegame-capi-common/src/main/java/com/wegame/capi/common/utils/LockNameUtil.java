package com.wegame.capi.common.utils;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;

public class LockNameUtil {

    // 定时任务锁
    private static final String BOXING_BET_RECORD_PAY_OUT_SCHEDULER_LOCK = "lock:boxing_bet_record_pay_out_scheduler_lock";
    private static final String BOXING_REWARD_GRANT_STATUS_SCHEDULER_LOCK = "lock:boxing_reward_grant_status_scheduler_lock";
    private static final String BOXING_PAYOUT_STATUS_SCHEDULER_LOCK = "lock:boxing_payout_status_scheduler_lock";
    private static final String BOXING_PAYOUT_RECORD_UN_PAYOUT_STATUS_SCHEDULER_LOCK = "lock:boxing_payout_record_un_payout_status_scheduler_lock";
    private static final String BOXING_BET_RECORD_BET_STATUS_SCHEDULER_LOCK = "lock:boxing_bet_record_bet_status_scheduler_lock";
    private static final String REWARD_POOL_RECONCILIATION_SCHEDULER_LOCK = "lock:reward_pool_reconciliation_scheduler_lock";
    private static final String DRAW_REWARD_POOL_RECONCILIATION_SCHEDULER_LOCK = "lock:draw_reward_pool_reconciliation_scheduler_lock";


    private static final String BOXING_BET_LOCK = "lock:boxing_bet_lock_%s_%d_%d";
    private static final String BOXING_PAY_OUT_LOCK = "lock:boxing_pay_out_lock_%d_%d";
    private static final String BOXING_BET_RECORD_PAY_OUT_LOCK = "lock:boxing_bet_record_pay_out_lock_%s";
    private static final String BOXING_AWARD_LOCK = "lock:boxing_award_lock_%s";
    private static final String REWARD_POOL_LOCK = "lock:reward_pool_lock:%s";

    private static final String GRANT_USER_REWARD_COUPON_LOCK = "lock:grant_user_reward_coupon_lock_%d";


    private LockNameUtil() {
    }

    /// ====== 分割线 定时任务锁 ====== ///

    public static String getBoxingBetRecordPayOutStatusSchedulerLockName() {
        return BOXING_BET_RECORD_PAY_OUT_SCHEDULER_LOCK;
    }

    public static String getBoxingBetRecordBetStatusSchedulerLock() {
        return BOXING_BET_RECORD_BET_STATUS_SCHEDULER_LOCK;
    }

    public static String getBoxingPayOutStatusSchedulerLockName() {
        return BOXING_PAYOUT_STATUS_SCHEDULER_LOCK;
    }

    public static String getBoxingPayOutRecordUnPayOutSchedulerLockName() {
        return BOXING_PAYOUT_RECORD_UN_PAYOUT_STATUS_SCHEDULER_LOCK;
    }

    public static String getBoxingRewardGrantStatusSchedulerLock() {
        return BOXING_REWARD_GRANT_STATUS_SCHEDULER_LOCK;
    }

    public static String getRewardPoolReconciliationSchedulerLock() {
        return REWARD_POOL_RECONCILIATION_SCHEDULER_LOCK;
    }

    public static String getDrawRewardPoolReconciliationSchedulerLock() {
        return DRAW_REWARD_POOL_RECONCILIATION_SCHEDULER_LOCK;
    }
    /// ====== 分割线 定时任务锁 ====== ///

    public static String getRewardPoolLockName(Integer rewardPoolType) {
        return String.format(REWARD_POOL_LOCK, rewardPoolType);
    }

    public static String getBoxingBetLockName(String playerId, Long boxingId, Long matchId) {
        AssertUtil.assertStrNotEmpty(playerId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(boxingId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(matchId, ErrorCode.ERR_PARAM);
        return String.format(BOXING_BET_LOCK, playerId, boxingId, matchId);
    }

    public static String getBoxingPayOutLockName(Long boxingId, Long matchId) {
        AssertUtil.assertNotNull(boxingId, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(matchId, ErrorCode.ERR_PARAM);
        return String.format(BOXING_PAY_OUT_LOCK, boxingId, matchId);
    }

    public static String getBoxingBetRecordPayOutLockName(Long betRecordId) {
        AssertUtil.assertNotNull(betRecordId, ErrorCode.ERR_PARAM);
        return String.format(BOXING_BET_RECORD_PAY_OUT_LOCK, betRecordId);
    }

    public static String getBoxingAwardLockName(Long userRewardRecordId) {
        return String.format(BOXING_AWARD_LOCK, userRewardRecordId);
    }

    public static String getGrantUserRewardCouponLockName(Long userId) {
        AssertUtil.assertNotNull(userId, ErrorCode.ERR_PARAM);
        return String.format(GRANT_USER_REWARD_COUPON_LOCK, userId);
    }

}
