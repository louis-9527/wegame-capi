package com.wegame.capi.application.controller;

import com.wegame.base.Validate;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.domain.PageInfoVo;

import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.biz.converter.business.GameRecordConverter;
import com.wegame.capi.biz.domain.business.GameRecordDo;
import com.wegame.capi.client.domain.request.query.PageQueryGameRecordRequest;
import com.wegame.capi.client.domain.vo.GameRecordVo;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 游戏记录模块
 * @author generate
 */
@Slf4j
@RestController
@RequestMapping("gameRecord")
@Api(value = "游戏记录模块", tags = {"游戏记录模块"})
public class GameRecordController {

    @Autowired
    GameRecordService gameRecordService;

    @ApiOperation(value = "新增（单条）", httpMethod = "POST")
    @PostMapping("/add")
    public CommonResponse<String> add(@RequestBody GameRecordVo vo) {
        vo.validate();
        Long id = gameRecordService.add(GameRecordConverter.voToDo(vo));
        return CommonResponse.build(String.valueOf(id));
    }

    @ApiOperation(value = "新增（批量）", httpMethod = "POST")
    @PostMapping("/batchAdd")
    public CommonResponse<Boolean> batchAdd(@RequestBody List<GameRecordVo> voList) {
        Optional.ofNullable(voList)
            .orElse(Collections.emptyList())
            .forEach(Validate::validate);
        return CommonResponse.build(gameRecordService.batchAdd(GameRecordConverter.voToDoList(voList)));
    }

    @ApiOperation(value = "删除（单条：根据唯一id）", httpMethod = "GET")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "主键id", required = true, dataTypeClass = String.class, paramType = "path")})
    @GetMapping("/delete/{id}")
    public CommonResponse<Boolean> delete(@PathVariable String id) {
        return CommonResponse.build(gameRecordService.delete(Long.valueOf(id)));
    }

    @ApiOperation(value = "删除（批量：根据唯一id列表）", httpMethod = "POST")
    @GetMapping("/batchDelete")
    public CommonResponse<Boolean> batchDelete(@RequestBody List<String> ids) {
        List<Long> idList = Optional.ofNullable(ids)
            .orElse(Collections.emptyList())
            .stream()
            .filter(StringUtils::isNotEmpty)
            .map(Long::parseLong)
            .collect(Collectors.toList());
        return CommonResponse.build(gameRecordService.batchDelete(idList));
    }

    @ApiOperation(value = "更新（单条：根据唯一id）", httpMethod = "POST")
    @PostMapping("/update")
    public CommonResponse<Boolean> update(@RequestBody GameRecordVo vo) {
        vo.validate();
        return CommonResponse.build(gameRecordService.update(GameRecordConverter.voToDo(vo)));
    }

    @ApiOperation(value = "查询（单条：根据唯一id）", httpMethod = "GET")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "主键id", required = true, dataTypeClass = String.class, paramType = "path")})
    @GetMapping("/get/{id}")
    public CommonResponse<GameRecordVo> get(@PathVariable String id) {
        return CommonResponse.build(GameRecordConverter.doToVo(gameRecordService.get(Long.valueOf(id))));
    }

    @ApiOperation(value = "查询（批量：根据唯一id列表）", httpMethod = "POST")
    @GetMapping("/listByIds")
    public CommonResponse<List<GameRecordVo>> listByIds(@RequestBody List<String> ids) {
        List<Long> idList = Optional.ofNullable(ids)
            .orElse(Collections.emptyList())
            .stream()
            .filter(StringUtils::isNotEmpty)
            .map(Long::parseLong)
            .collect(Collectors.toList());
        return CommonResponse.build(GameRecordConverter.doToVoList(gameRecordService.listByIds(idList)));
    }

    @ApiOperation(value = "分页查询（批量：根据查询条件）", httpMethod = "POST")
    @PostMapping("/pageQuery")
    public CommonResponse<PageInfoVo<GameRecordVo>> pageQuery(@RequestBody PageQueryGameRecordRequest request) {
        request.validate();
        GameRecordQuery query = GameRecordConverter.requestToQuery(request, "order by `create_time` desc");
        long cnt = gameRecordService.count(query);
        List<GameRecordDo> list = gameRecordService.list(query);
        return CommonResponse.build(PageInfoVo.build(request.getPageNo(),
                            request.getPageSize(),
                            GameRecordConverter.doToVoList(list),
                            cnt));
    }

    ///////////////  above auto generated ///////////////
}