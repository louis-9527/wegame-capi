package com.wegame.capi.test.application.controller;

import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.application.controller.RewardController;
import com.wegame.capi.client.domain.vo.RewardInfoVo;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

@Slf4j
public class RewardVoControllerTest extends BaseTest {

    @Autowired
    public RewardController rewardController;

    protected void commonGet() {
        HttpServletResponse httpServletResponse = new MockHttpServletResponse();
        CommonResponse<RewardInfoVo> commonResponse = rewardController.get(httpServletResponse);
        Assert.assertNotNull(commonResponse);
        Assert.assertTrue(StringUtils.equals(commonResponse.getCode(), CommonResponse.SUC_CODE));
        Assert.assertNotNull(commonResponse.getData());
    }
}
