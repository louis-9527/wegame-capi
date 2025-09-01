package com.wegame.capi.application.controller.platform;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.helper.DynamicOperationEncryptionHelper;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.client.domain.request.query.GetStatusOfEventEncryptRequest;
import com.wegame.capi.client.domain.request.query.GetStatusOfEventRequest;
import com.wegame.capi.client.domain.response.PlatformResponse;
import com.wegame.capi.client.domain.vo.StatusOfEventsResultVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.OperationEventStatusEnum;
import com.wegame.capi.dao.repository.domain.operation.OperationEventQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static com.wegame.capi.biz.converter.operation.StatusOfEventsConverter.doToVoList;
import static com.wegame.capi.client.domain.response.PlatformResponse.build;

/**
 * 干预逻辑：
 * 1、干预接口
 * 2、干预消息
 * 3、干预投注
 */
@Slf4j
@RestController
@RequestMapping("operationEvent")
@Api(value = "干预事件回调模块", tags = {"干预事件回调模块"})
public class OperationEventCallbackController {


    public static final Map<GameTypeEnum, String> OPERATION_EVENT_MANAGER_NAME_MAP = new ImmutableMap.Builder<GameTypeEnum, String>()
            .put(GameTypeEnum.MARIA_MAKILING, "mariaMakilingOperationEventManager")
            .build();

    @Autowired
    Map<String, AbstractOperationEventManager> operationEventManagerMap;

    @Autowired
    DynamicOperationEncryptionHelper dynamicOperationEncryptionHelper;

    /**
     * 干预事件回调接口
     * 主要功能：
     * @param request
     * @param encryptRequest
     * @return
     */
    @ApiOperation(value = "获取干预事件结果", httpMethod = "POST")
    @PostMapping("/getStatusOfEvents")
    public PlatformResponse<List<StatusOfEventsResultVo>> getStatusOfEvents(HttpServletRequest request,
                                                                            @RequestBody GetStatusOfEventEncryptRequest encryptRequest) {
        encryptRequest.validate();
        String encryptedKey = request.getHeader("keyString");
        String encryptedIv = request.getHeader("iv");
        AssertUtil.assertStrNotEmpty(encryptedKey, ErrorCode.ERR_PARAM);
        AssertUtil.assertStrNotEmpty(encryptedIv, ErrorCode.ERR_PARAM);
        String agentCode = encryptRequest.getAgentCode();
        // 使用私钥解密 encryptedKey 和 encryptedIv
        String keyString = dynamicOperationEncryptionHelper.decrypt(agentCode,encryptedKey);
        String ivString = dynamicOperationEncryptionHelper.decrypt(agentCode,encryptedIv);
        // String key 和iv 解密message
        String messageBody = dynamicOperationEncryptionHelper.decryptWithAES256CBC(encryptRequest.getMessage(), keyString, ivString);
        GetStatusOfEventRequest eventRequest = JSON.parseObject(messageBody, GetStatusOfEventRequest.class);
        eventRequest.validate();
        OperationEventQuery operationEventQuery = OperationEventQuery.builder()
                .eventIdList(eventRequest.getEventIds())
                .eventStatus(OperationEventStatusEnum.FINISH.getCode())
                .effectedStartTime(eventRequest.getStartTime() == null ? null : DateUtil.utcStringToEpochMilli(eventRequest.getStartTime()))
                .effectedEndTime(eventRequest.getEndTime() == null ? null : DateUtil.utcStringToEpochMilli(eventRequest.getEndTime()))
                .build();
        return build(doToVoList(this.operationEventManager(GameTypeEnum.valueOfId(eventRequest.getGameId())).getStatusOfEvents(operationEventQuery)));
    }

    private AbstractOperationEventManager operationEventManager(GameTypeEnum gameType) {
        AbstractOperationEventManager eventManager = operationEventManagerMap.get(OPERATION_EVENT_MANAGER_NAME_MAP.get(gameType));
        AssertUtil.assertNotNull(eventManager, ErrorCode.ERR_INTER);
        return eventManager;
    }
}
