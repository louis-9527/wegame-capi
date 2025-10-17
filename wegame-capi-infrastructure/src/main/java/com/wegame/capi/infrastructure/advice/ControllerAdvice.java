package com.wegame.capi.infrastructure.advice;

import com.wegame.base.domain.CommonResponse;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统一异常处理
 * @author apple
 */
@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ControllerAdvice {

    @ExceptionHandler({BusinessException.class})
    public CommonResponse handleBusinessException(BusinessException ex) {
        log.error("Got BusinessException: [{}], errCode: {}", ex.getMessage(), ex.getErrCode(), ex);
        return CommonResponse.build(ex.getErrCode(), StringUtils.isNotBlank(ex.getMessage()) ? ex.getMessage() : ex.getErrMsg());
    }

    @ExceptionHandler({MyBatisSystemException.class})
    public CommonResponse handleMyBatisSystemException(Exception ex) {
        log.error("Got MyBatisSystemException: [{}]", ex.getMessage(), ex);
        return CommonResponse.build(ErrorCode.ERR_INTER.getCode(), ErrorCode.ERR_INTER.getMsg());
    }

    @ExceptionHandler({PersistenceException.class})
    public CommonResponse handlePersistenceException(Exception ex) {
        log.error("Got PersistenceException: [{}]", ex.getMessage(), ex);
        return CommonResponse.build(ErrorCode.ERR_INTER.getCode(), ErrorCode.ERR_INTER.getMsg());
    }

    @ExceptionHandler({RetryException.class})
    public CommonResponse handleRetryException(Exception ex) {
        log.error("Got RetryException: [{}]", ex.getMessage(), ex);
        return CommonResponse.build(ErrorCode.ERR_INTER.getCode(), ErrorCode.ERR_INTER.getMsg());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public CommonResponse handleException(MethodArgumentNotValidException ex) {
        log.error("Got MethodArgumentNotValidException: [{}]", ex.getMessage(), ex);
        BindingResult exceptions = ex.getBindingResult();
        // 判断异常中是否有错误信息，如果存在就使用异常中的消息，否则使用默认消息
        List<String> errList = new ArrayList<>();
        if (exceptions.hasErrors()) {
            List<ObjectError> errors = exceptions.getAllErrors();
            if (!errors.isEmpty()) {
                for (ObjectError error : errors) {
                    // 这里列出了全部错误参数，按正常逻辑，只需要第一条错误即可
                    FieldError fieldError = (FieldError) error;
                    errList.add(fieldError.getDefaultMessage());
                }
            }
        }
        return CommonResponse.build(ErrorCode.ERR_PARAM.getCode(), String.join("; ", errList));
    }

    @ExceptionHandler({Exception.class})
    public CommonResponse handleException(Exception ex) {
        log.error("Got Exception [{}]", ex.getMessage(), ex);
        return CommonResponse.build(ErrorCode.ERR_INTER.getCode(), ErrorCode.ERR_INTER.getMsg());
    }

    @ExceptionHandler({RuntimeException.class})
    public CommonResponse handleRuntimeException(RuntimeException ex) {
        log.error("Got RuntimeException [{}] ", ex.getMessage(), ex);
        return CommonResponse.build(ErrorCode.ERR_INTER.getCode(), ErrorCode.ERR_INTER.getMsg());
    }
}
