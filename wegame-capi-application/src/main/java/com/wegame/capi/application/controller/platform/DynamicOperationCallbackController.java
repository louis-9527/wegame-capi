package com.wegame.capi.application.controller.platform;

import com.wegame.capi.biz.converter.PlatformCallbackConverter;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationStatusOfEventsDo;
import com.wegame.capi.biz.manager.DynamicOperationManager;
import com.wegame.capi.client.domain.request.query.GetStatusOfEventRequest;
import com.wegame.capi.client.domain.response.DynamicOperationResponse;
import com.wegame.capi.client.domain.vo.StatusOfEventsResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Api(value = "干预事件回调", tags = {"干预事件回调模块"})
public class DynamicOperationCallbackController {

    @Autowired
    DynamicOperationManager dynamicOperationManager;

    @ApiOperation(value = "取干预事件结果", httpMethod = "POST")
    @PostMapping("/getStatusOfEvents")
    public DynamicOperationResponse<List<StatusOfEventsResultVo>> getStatusOfEvents(@RequestBody GetStatusOfEventRequest request) {
        request.validate();
        List<DynamicOperationStatusOfEventsDo> dynamicOperationStatusOfEventsDos = dynamicOperationManager.getStatusOfEvents(request);
        return DynamicOperationResponse.build(PlatformCallbackConverter.dynamicOperationStatusOfEventsDoToVos(dynamicOperationStatusOfEventsDos));
    }
}
