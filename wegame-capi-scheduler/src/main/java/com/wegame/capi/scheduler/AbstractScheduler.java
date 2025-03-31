package com.wegame.capi.scheduler;

import cn.hutool.core.lang.UUID;
import com.wegame.base.Constants;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.exception.LockException;
import com.wegame.base.lock.DistributeLock;
import com.wegame.base.utils.ThreadMdcUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
public abstract class AbstractScheduler {

    public static final int DEFAULT_PAGE_SIZE = 100;
    private static final String LOCK_FAIL_TIP = "Get lock[%s] fail";
    @Autowired
    DistributeLock distributeLock;

    public String taskName() {
        return getClass().getSimpleName();
    }

    protected void scheduleWithLock(String lockName, Runnable runnable) {
        distributeLock.lockThenRun(lockName, () -> {
            try {
                log.info("schedule task, get lock suc, taskName: {}, lockName: {}", taskName(), lockName);
                MDC.put(ThreadMdcUtil.TRACE_ID, UUID.randomUUID().toString(true));
                OperatorContext.setOperator(Constants.SYSTEM_OPERATOR);
                runnable.run();
            } catch (LockException e) {
                log.info("schedule task warn, get lock fail, taskName: {}, lockName: {}", taskName(), lockName);
            } catch (Exception e) {
                log.error("{} schedule task error, errMsg: {}", taskName(), e.getMessage(), e);
            } finally {
                MDC.clear();
                OperatorContext.clear();
            }
        }, String.format(LOCK_FAIL_TIP, lockName));
    }
}
