//package com.wegame.capi.common.exception;
//
//import com.wegame.base.enums.ErrorCode;
//import com.wegame.base.exception.BaseException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.retry.RetryContext;
//import org.springframework.retry.support.RetrySynchronizationManager;
//
//import java.util.Objects;
//
///**
// * 业务重试异常
// * 使用说明：此异常限制在需要重试的方法使用，
// *
// * @author jayni
// */
//@Slf4j
//public class RetryException extends BaseException {
//
//    public RetryException(ErrorCode errorCode, Exception e) {
//        super(errorCode, e);
//        retry();
//    }
//
//    public RetryException(ErrorCode errorCode, String msg, Exception e) {
//        super(errorCode, msg, e);
//        //重试日志记录
//        retry();
//    }
//
//    public RetryException(ErrorCode errorCode, Object params) {
//        super(errorCode, params);
//        retry();
//    }
//
//    public void retry() {
//        RetryContext context = RetrySynchronizationManager.getContext();
//        if (Objects.isNull(context)) {
//            throw new IllegalArgumentException("retry error,you can not use minesRetryException alone,please use with annotation MinesRetry");
//        }
//
//        StackTraceElement stackTraceElement = this.getStackTrace()[0];
//        String methodName = stackTraceElement.getMethodName();
//        int retryCount = context.getRetryCount();
//        log.info("{} start retry,hasRetryCount:{}", methodName, retryCount);
//    }
//
//}
