package com.wegame.capi.application.controller.promotion;

import com.wegame.base.annotation.LoginRequired;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.domain.PageDo;
import com.wegame.base.domain.PageInfoVo;
import com.wegame.capi.biz.converter.promotion.BagOtherRewardDetailConverter;
import com.wegame.capi.biz.converter.promotion.BagRewardConverter;
import com.wegame.capi.biz.converter.promotion.level.LevelConfigConverter;
import com.wegame.capi.biz.converter.promotion.level.PlayerLevelConverter;
import com.wegame.capi.biz.domain.biz.promotion.BagCurrentRewardDetailDo;
import com.wegame.capi.biz.domain.biz.promotion.HistoryMessageDo;
import com.wegame.capi.biz.domain.biz.promotion.level.LevelConfigDo;
import com.wegame.capi.biz.service.promotion.BagService;
import com.wegame.capi.biz.service.promotion.LevelService;
import com.wegame.capi.client.domain.request.promotion.QueryBagCurrentGameRewardRequest;
import com.wegame.capi.client.domain.request.promotion.QueryBagOtherGameRewardRequest;
import com.wegame.capi.client.domain.request.promotion.QueryHistoryMessageRequest;
import com.wegame.capi.client.domain.request.promotion.QueryRewardDetailRequest;
import com.wegame.capi.client.domain.vo.promotion.BagCurrentRewardVo;
import com.wegame.capi.client.domain.vo.promotion.BagOtherRewardVo;
import com.wegame.capi.client.domain.vo.promotion.HistoryMessageVo;
import com.wegame.capi.client.domain.vo.promotion.RewardDetailVo;
import com.wegame.capi.client.domain.vo.promotion.level.LevelConfigVo;
import com.wegame.capi.client.domain.vo.promotion.level.PlayerLevelVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.wegame.base.domain.CommonResponse.build;
import static com.wegame.capi.biz.converter.promotion.HistoryMessageConverter.doToVoList;
import static com.wegame.capi.biz.converter.promotion.RewardDetailConverter.doToVo;

@Slf4j
@RestController
@RequestMapping("level")
@Api(value = "等级模块", tags = {"等级模块"})
public class LevelController {

    @Autowired
    LevelService levelService;

    @LoginRequired
    @ApiOperation(value = "获取等级配置", httpMethod = "POST")
    @PostMapping("/getLevelConfig")
    public CommonResponse<LevelConfigVo> getLevelConfig() {
        return build(LevelConfigConverter.doToVo(levelService.getLevelConfig()));
    }

    @LoginRequired
    @ApiOperation(value = "获取用户等级", httpMethod = "POST")
    @PostMapping("/getPlayerLevel")
    public CommonResponse<PlayerLevelVo> getPlayerLevel() {
        return build(PlayerLevelConverter.doToVo(levelService.getPlayerLevel()));
    }

}
