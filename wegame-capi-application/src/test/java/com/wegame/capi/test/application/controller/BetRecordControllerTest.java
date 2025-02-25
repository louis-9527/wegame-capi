package com.wegame.capi.test.application.controller;

import cn.hutool.core.lang.Assert;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.domain.PageInfoVo;
import com.wegame.capi.application.controller.BetRecordController;
import com.wegame.capi.client.domain.request.query.PageQueryBetRecordRequest;
import com.wegame.capi.client.domain.vo.BetRecordVo;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class BetRecordControllerTest extends BaseTest {
    @Autowired
    public BetRecordController betRecordController;

    public void commonPageQuery() {
        PageQueryBetRecordRequest request = buildPageQueryBetRecordRequest();
        CommonResponse<PageInfoVo<BetRecordVo>> commonResponse = betRecordController.pageQuery(request);
        Assert.notNull(commonResponse);
        Assert.notNull(commonResponse.getData());
        Assert.isTrue(StringUtils.equals(commonResponse.getCode(), CommonResponse.SUC_CODE));
        Assert.notNull(commonResponse.getData());
        Assert.isTrue(CollectionUtils.isNotEmpty(commonResponse.getData().getList()));
    }

    public BetRecordVo commonGet(String id) {
        CommonResponse<BetRecordVo> commonResponse = betRecordController.get(id);
        Assert.notNull(commonResponse);
        Assert.notNull(commonResponse.getData());
        Assert.isTrue(StringUtils.equals(commonResponse.getCode(), CommonResponse.SUC_CODE));
        Assert.notNull(commonResponse.getData());
        return commonResponse.getData();
    }

    private PageQueryBetRecordRequest buildPageQueryBetRecordRequest() {
        PageQueryBetRecordRequest request = new PageQueryBetRecordRequest();
        request.setPageNo(1);
        request.setPageSize(20);
        request.setEndCreateTime(System.currentTimeMillis());
        request.setStartCreateTime(System.currentTimeMillis() - 5 * 60 * 1000);
        return request;
    }
}
