package com.wegame.capi.test.application.controller;

import cn.hutool.core.lang.Assert;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.application.controller.RewardController;
import com.wegame.capi.client.domain.vo.RewardInfoVo;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

@Slf4j
public class RewardControllerTest extends BaseTest {
    @Autowired
    public RewardController rewardController;


    public void commonGet() {
        HttpServletResponse httpServletResponse = new MockHttpServletResponse();
        CommonResponse<RewardInfoVo> commonResponse = rewardController.get(httpServletResponse);
        Assert.notNull(commonResponse);
        Assert.isTrue(StringUtils.equals(commonResponse.getCode(), CommonResponse.SUC_CODE));
        Assert.notNull(commonResponse.getData());
    }
}
