package com.wegame.capi.application.controller.promotion;

import com.wegame.capi.biz.service.promotion.MissionService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("mission")
@Api(value = "营销任务模块", tags = {"营销任务模块"})
public class MissionController {

    @Autowired
    MissionService missionService;

}
