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
@Component
public abstract class AbstractScheduler {

    public static final int DEFAULT_PAGE_SIZE = 20;

    private static final String LOCK_FAIL_TIP = "Get lock[%s] fail";

    @Autowired
    DistributeLock distributeLock;

    String taskName() {
        return this.getClass().getSimpleName();
    }

    protected void scheduleWithLock(String lockName, Runnable runnable) {
        distributeLock.lockThenRun(lockName, () -> {
            try {
                MDC.put(ThreadMdcUtil.TRACE_ID, UUID.randomUUID().toString(true));
                OperatorContext.setOperator(Constants.SYSTEM_OPERATOR);
                runnable.run();
            } catch (LockException e) {
                log.warn("{} schedule task error, get lock fail, taskName: {}, lockName: {}", taskName(), lockName);
            } catch (Exception e) {
                log.error("{} schedule task error, errMsg: {}", taskName(), e.getMessage(), e);
            } finally {
                MDC.clear();
                OperatorContext.clear();
            }
        }, String.format(LOCK_FAIL_TIP, lockName));
    }


}
