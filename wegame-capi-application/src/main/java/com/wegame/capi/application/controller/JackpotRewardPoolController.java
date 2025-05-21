package com.wegame.capi.application.controller;

import com.wegame.base.Validate;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.domain.PageInfoVo;

import com.wegame.capi.biz.service.JackpotRewardPoolService;
import com.wegame.capi.biz.converter.business.JackpotRewardPoolConverter;
import com.wegame.capi.biz.domain.business.JackpotRewardPoolDo;
import com.wegame.capi.client.domain.request.query.PageQueryJackpotRewardPoolRequest;
import com.wegame.capi.client.domain.vo.JackpotRewardPoolVo;
import com.wegame.capi.dao.mapper.domain.business.JackpotRewardPoolQuery;
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
 * jackpot奖池模块
 * @author generate
 */
@Slf4j
@RestController
@RequestMapping("jackpotRewardPool")
@Api(value = "jackpot奖池模块", tags = {"jackpot奖池模块"})
public class JackpotRewardPoolController {

    @Autowired
    JackpotRewardPoolService jackpotRewardPoolService;

    @ApiOperation(value = "新增（单条）", httpMethod = "POST")
    @PostMapping("/add")
    public CommonResponse<Boolean> add(@RequestBody JackpotRewardPoolVo vo) {
        vo.validate();
        Long id = jackpotRewardPoolService.add(JackpotRewardPoolConverter.voToDo(vo));
        return CommonResponse.build(Objects.nonNull(id));
    }

    @ApiOperation(value = "新增（批量）", httpMethod = "POST")
    @PostMapping("/batchAdd")
    public CommonResponse<Boolean> batchAdd(@RequestBody List<JackpotRewardPoolVo> voList) {
        Optional.ofNullable(voList)
            .orElse(Collections.emptyList())
            .forEach(Validate::validate);
        return CommonResponse.build(jackpotRewardPoolService.batchAdd(JackpotRewardPoolConverter.voToDoList(voList)));
    }

    @ApiOperation(value = "删除（单条：根据唯一id）", httpMethod = "GET")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "主键id", required = true, dataTypeClass = String.class, paramType = "path")})
    @GetMapping("/delete/{id}")
    public CommonResponse<Boolean> delete(@PathVariable String id) {
        return CommonResponse.build(jackpotRewardPoolService.delete(Long.valueOf(id)));
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
        return CommonResponse.build(jackpotRewardPoolService.batchDelete(idList));
    }

    @ApiOperation(value = "更新（单条：根据唯一id）", httpMethod = "POST")
    @PostMapping("/update")
    public CommonResponse<Boolean> update(@RequestBody JackpotRewardPoolVo vo) {
        vo.validate();
        return CommonResponse.build(jackpotRewardPoolService.update(JackpotRewardPoolConverter.voToDo(vo)));
    }

    @ApiOperation(value = "查询（单条：根据唯一id）", httpMethod = "GET")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "主键id", required = true, dataTypeClass = String.class, paramType = "path")})
    @GetMapping("/get/{id}")
    public CommonResponse<JackpotRewardPoolVo> get(@PathVariable String id) {
        return CommonResponse.build(JackpotRewardPoolConverter.doToVo(jackpotRewardPoolService.get(Long.valueOf(id))));
    }

    @ApiOperation(value = "查询（批量：根据唯一id列表）", httpMethod = "POST")
    @GetMapping("/listByIds")
    public CommonResponse<List<JackpotRewardPoolVo>> listByIds(@RequestBody List<String> ids) {
        List<Long> idList = Optional.ofNullable(ids)
            .orElse(Collections.emptyList())
            .stream()
            .filter(StringUtils::isNotEmpty)
            .map(Long::parseLong)
            .collect(Collectors.toList());
        return CommonResponse.build(JackpotRewardPoolConverter.doToVoList(jackpotRewardPoolService.listByIds(idList)));
    }

    @ApiOperation(value = "分页查询（批量：根据查询条件）", httpMethod = "POST")
    @PostMapping("/pageQuery")
    public CommonResponse<PageInfoVo<JackpotRewardPoolVo>> pageQuery(@RequestBody PageQueryJackpotRewardPoolRequest request) {
        request.validate();
        JackpotRewardPoolQuery query = JackpotRewardPoolConverter.requestToQuery(request, "order by `create_time` desc");
        long cnt = jackpotRewardPoolService.count(query);
        List<JackpotRewardPoolDo> list = jackpotRewardPoolService.list(query);
        return CommonResponse.build(PageInfoVo.build(request.getPageNo(),
                            request.getPageSize(),
                            JackpotRewardPoolConverter.doToVoList(list),
                            cnt));
    }

    ///////////////  above auto generated ///////////////
}