package com.wegame.capi.application.controller.platform;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.helper.DynamicOperationEncryptionHelper;
import com.wegame.capi.biz.manager.operation.MariaMakilingOperationEventManager;
import com.wegame.capi.client.domain.request.query.GetStatusOfEventEncryptRequest;
import com.wegame.capi.client.domain.request.query.GetStatusOfEventRequest;
import com.wegame.capi.client.domain.response.PlatformResponse;
import com.wegame.capi.client.domain.vo.StatusOfEventsResultVo;
import com.wegame.capi.common.enums.OperationEventStatusEnum;
import com.wegame.capi.dao.mapper.domain.operation.OperationEventQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.wegame.capi.biz.converter.operation.StatusOfEventsConverter.doToVoList;
import static com.wegame.capi.client.domain.response.PlatformResponse.build;

@Slf4j
@RestController
@RequestMapping("operationEvent")
@Api(value = "干预事件回调模块", tags = {"干预事件回调模块"})
public class OperationEventCallbackController {

    // todo 支持多游戏
    @Autowired
    @Qualifier("mariaMakilingOperationEventManager")
    MariaMakilingOperationEventManager mariaMakilingOperationEventManager;
    @Autowired
    DynamicOperationEncryptionHelper dynamicOperationEncryptionHelper;

    @ApiOperation(value = "获取干预事件结果", httpMethod = "POST")
    @PostMapping("/getStatusOfEvents")
    public PlatformResponse<List<StatusOfEventsResultVo>> getStatusOfEvents(HttpServletRequest request,
                                                                            @RequestBody GetStatusOfEventEncryptRequest encryptRequest) {
        encryptRequest.validate();
        String encryptedKey = request.getHeader("keyString");
        String encryptedIv = request.getHeader("iv");
        AssertUtil.assertStrNotEmpty(encryptedKey, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(encryptedIv, ErrorCode.ERR_PARAM);
        //使用私钥解密encryptedKey 和 encryptedIv
        String keyString = dynamicOperationEncryptionHelper.decrypt(encryptedKey);
        String ivString = dynamicOperationEncryptionHelper.decrypt(encryptedIv);
        //String key 和iv 解密message
        String messageBody = dynamicOperationEncryptionHelper.decryptWithAES256CBC(encryptRequest.getMessage(), keyString, ivString);
        GetStatusOfEventRequest eventRequest = JSON.parseObject(messageBody, GetStatusOfEventRequest.class);
        eventRequest.validate();
        // todo 支持多种游戏 设置gameId参数
        OperationEventQuery operationEventQuery = OperationEventQuery.builder()
                .eventIdList(eventRequest.getEventIds())
                .eventStatus(OperationEventStatusEnum.FINISH.getCode())
                .effectedStartTime(eventRequest.getStartTime().toInstant().toEpochMilli())
                .effectedEndTime(eventRequest.getEndTime().toInstant().toEpochMilli())
                .build();
        return build(doToVoList(mariaMakilingOperationEventManager.getStatusOfEvents(operationEventQuery)));
    }
}
