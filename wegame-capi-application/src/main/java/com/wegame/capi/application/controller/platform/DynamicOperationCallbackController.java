package com.wegame.capi.application.controller.platform;

import com.wegame.capi.biz.manager.MariaMakilingDynamicOperationManager;
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

import static com.wegame.capi.biz.converter.StatusOfEventsConverter.toVoList;
import static com.wegame.capi.client.domain.response.DynamicOperationResponse.build;

@Slf4j
@RestController
@Api(value = "干预事件回调模块", tags = {"干预事件回调模块"})
public class DynamicOperationCallbackController {

    @Autowired
    MariaMakilingDynamicOperationManager mariaMakilingDynamicOperationManager;

    // todo 支持多游戏

    @ApiOperation(value = "获取干预事件结果", httpMethod = "POST")
    @PostMapping("/getStatusOfEvents")
    public DynamicOperationResponse<List<StatusOfEventsResultVo>> getStatusOfEvents(@RequestBody GetStatusOfEventRequest request) {
        request.validate();
        return build(toVoList(mariaMakilingDynamicOperationManager.getStatusOfEvents(request)));
    }
}
