//package com.wegame.capi.application.controller.jackpot;
//
//import com.wegame.base.Validate;
//import com.wegame.base.domain.CommonResponse;
//import com.wegame.base.domain.PageInfoVo;
//
//import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolInputRecordService;
//import com.wegame.capi.biz.converter.jackpot.JackpotRewardPoolInputRecordConverter;
//import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
//import com.wegame.capi.client.domain.request.query.PageQueryJackpotRewardPoolInputRecordRequest;
//import com.wegame.capi.client.domain.vo.jackpot.JackpotRewardPoolInputRecordVo;
//import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolInputRecordQuery;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * jackpot奖池流入记录模块
// * @author generate
// */
//@Slf4j
//@RestController
//@RequestMapping("jackpotRewardPoolInputRecord")
//@Api(value = "jackpot奖池流入记录模块", tags = {"jackpot奖池流入记录模块"})
//public class JackpotRewardPoolInputRecordController {
//
//    @Autowired
//    JackpotRewardPoolInputRecordService jackpotRewardPoolInputRecordService;
//
//    @ApiOperation(value = "新增（单条）", httpMethod = "POST")
//    @PostMapping("/add")
//    public CommonResponse<Boolean> add(@RequestBody JackpotRewardPoolInputRecordVo vo) {
//        vo.validate();
//        Long id = jackpotRewardPoolInputRecordService.add(JackpotRewardPoolInputRecordConverter.voToDo(vo));
//        return CommonResponse.build(Objects.nonNull(id));
//    }
//
//    @ApiOperation(value = "新增（批量）", httpMethod = "POST")
//    @PostMapping("/batchAdd")
//    public CommonResponse<Boolean> batchAdd(@RequestBody List<JackpotRewardPoolInputRecordVo> voList) {
//        Optional.ofNullable(voList)
//            .orElse(Collections.emptyList())
//            .forEach(Validate::validate);
//        return CommonResponse.build(jackpotRewardPoolInputRecordService.batchAdd(JackpotRewardPoolInputRecordConverter.voToDoList(voList)));
//    }
//
//    @ApiOperation(value = "删除（单条：根据唯一id）", httpMethod = "GET")
//    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "主键id", required = true, dataTypeClass = String.class, paramType = "path")})
//    @GetMapping("/delete/{id}")
//    public CommonResponse<Boolean> delete(@PathVariable String id) {
//        return CommonResponse.build(jackpotRewardPoolInputRecordService.delete(Long.valueOf(id)));
//    }
//
//    @ApiOperation(value = "删除（批量：根据唯一id列表）", httpMethod = "POST")
//    @GetMapping("/batchDelete")
//    public CommonResponse<Boolean> batchDelete(@RequestBody List<String> ids) {
//        List<Long> idList = Optional.ofNullable(ids)
//            .orElse(Collections.emptyList())
//            .stream()
//            .filter(StringUtils::isNotEmpty)
//            .map(Long::parseLong)
//            .collect(Collectors.toList());
//        return CommonResponse.build(jackpotRewardPoolInputRecordService.batchDelete(idList));
//    }
//
//    @ApiOperation(value = "更新（单条：根据唯一id）", httpMethod = "POST")
//    @PostMapping("/update")
//    public CommonResponse<Boolean> update(@RequestBody JackpotRewardPoolInputRecordVo vo) {
//        vo.validate();
//        return CommonResponse.build(jackpotRewardPoolInputRecordService.update(JackpotRewardPoolInputRecordConverter.voToDo(vo)));
//    }
//
//    @ApiOperation(value = "查询（单条：根据唯一id）", httpMethod = "GET")
//    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "主键id", required = true, dataTypeClass = String.class, paramType = "path")})
//    @GetMapping("/get/{id}")
//    public CommonResponse<JackpotRewardPoolInputRecordVo> get(@PathVariable String id) {
//        return CommonResponse.build(JackpotRewardPoolInputRecordConverter.doToVo(jackpotRewardPoolInputRecordService.get(Long.valueOf(id))));
//    }
//
//    @ApiOperation(value = "查询（批量：根据唯一id列表）", httpMethod = "POST")
//    @GetMapping("/listByIds")
//    public CommonResponse<List<JackpotRewardPoolInputRecordVo>> listByIds(@RequestBody List<String> ids) {
//        List<Long> idList = Optional.ofNullable(ids)
//            .orElse(Collections.emptyList())
//            .stream()
//            .filter(StringUtils::isNotEmpty)
//            .map(Long::parseLong)
//            .collect(Collectors.toList());
//        return CommonResponse.build(JackpotRewardPoolInputRecordConverter.doToVoList(jackpotRewardPoolInputRecordService.listByIds(idList)));
//    }
//
//    @ApiOperation(value = "分页查询（批量：根据查询条件）", httpMethod = "POST")
//    @PostMapping("/pageQuery")
//    public CommonResponse<PageInfoVo<JackpotRewardPoolInputRecordVo>> pageQuery(@RequestBody PageQueryJackpotRewardPoolInputRecordRequest request) {
//        request.validate();
//        JackpotRewardPoolInputRecordQuery query = JackpotRewardPoolInputRecordConverter.requestToQuery(request, "order by `create_time` desc");
//        long cnt = jackpotRewardPoolInputRecordService.count(query);
//        List<JackpotRewardPoolInputRecordDo> list = jackpotRewardPoolInputRecordService.list(query);
//        return CommonResponse.build(PageInfoVo.build(request.getPageNo(),
//                            request.getPageSize(),
//                            JackpotRewardPoolInputRecordConverter.doToVoList(list),
//                            cnt));
//    }
//
//    ///////////////  above auto generated ///////////////
//}