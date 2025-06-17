package com.wegame.capi.test.biz.module.diwatamayari.reward;

import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.module.diawatamayari.reward.DiwataMayariRewardHandler;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class DiwataMayariRewardHandlerTest extends BaseTest {
    @Autowired
    private DiwataMayariRewardHandler diwataMayariRewardHandler;

    /**
     * 中奖线数量测试
     * case1:不中
     * case2:中一个
     * case3:中多个
     * case4:中全部
     */
    @Test
    public void handler_normal_somLines_test(){
        DiwataMayariSymbolDo[][] diwataMayariSymbolDos = DiwataMayariRewardHandlerTestMocker.buildSymbolArr_zeroLine_test();
        RewardResultDo<DiwataMayariSymbolDo> zeroLineResult = diwataMayariRewardHandler.handle(DiwataMayariRewardHandlerTestMocker.buildRewardHandleDo(diwataMayariSymbolDos, 1000L, 0, false, BigDecimal.ONE));




    }


}
