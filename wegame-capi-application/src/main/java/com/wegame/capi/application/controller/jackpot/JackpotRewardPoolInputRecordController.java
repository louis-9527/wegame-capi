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