package com.wegame.capi.test.biz.module.ibongadarna.reward;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.IbongAdarnaSymbolDo;
import com.wegame.capi.biz.module.ibongadarna.IbongAdarnaGameHelper;
import com.wegame.capi.biz.module.ibongadarna.reward.IbongAdarnaRewardHandler;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.enums.SymbolBreakUpEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@Slf4j
public class IbongAdarnaRewardHandlerTest extends BaseTest {

    @Resource()
    IbongAdarnaRewardHandler ibongAdarnaRewardHandler;

    @Test
    public void handler_normal_oneLine_test() {
        IbongAdarnaSymbolDo[][] symbolArr = RewardTestMocker.mockSymbolArr_handler_normal_oneLine_test();
        RewardResultDo<IbongAdarnaSymbolDo> resultDo = ibongAdarnaRewardHandler.handle(RewardTestMocker.buildTestRewardHandleDo(BetSubTypeEnum.NORMAL_BET, symbolArr, 100L));
        assertNotNull(resultDo);
        assertTrue(CollectionUtil.isNotEmpty(resultDo.getRewardLineDetailList()));
        assertEquals(1, resultDo.getRewardLineDetailList().size());
        assertEquals(IbongAdarnaSymbolDo.buildIbongSymbolDo(IbongAdarnaGameHelper.IBONG_ADARNA_GAME_INFO.getSymbolById(SymbolEnum.Symbol1001.getId()), SymbolBreakUpEnum.NO_BREAK_UP),
                resultDo.getRewardLineDetailList().get(0).getRewardSymbol());
    }

    @Test
    public void handler_normal_twoLine_test() {
        IbongAdarnaSymbolDo[][] symbolArr = RewardTestMocker.mockSymbolArr_handler_normal_twoLine_test();
        RewardResultDo<IbongAdarnaSymbolDo> resultDo = ibongAdarnaRewardHandler.handle(RewardTestMocker.buildTestRewardHandleDo(BetSubTypeEnum.NORMAL_BET, symbolArr, 100L));
        assertNotNull(resultDo);
        assertTrue(CollectionUtil.isNotEmpty(resultDo.getRewardLineDetailList()));
        assertEquals(2, resultDo.getRewardLineDetailList().size());
        assertEquals(IbongAdarnaSymbolDo.buildIbongSymbolDo(IbongAdarnaGameHelper.IBONG_ADARNA_GAME_INFO.getSymbolById(SymbolEnum.Symbol1001.getId()), SymbolBreakUpEnum.NO_BREAK_UP),
                resultDo.getRewardLineDetailList().get(0).getRewardSymbol());
        assertEquals(IbongAdarnaSymbolDo.buildIbongSymbolDo(IbongAdarnaGameHelper.IBONG_ADARNA_GAME_INFO.getSymbolById(SymbolEnum.Symbol1002.getId()), SymbolBreakUpEnum.NO_BREAK_UP),
                resultDo.getRewardLineDetailList().get(1).getRewardSymbol());
    }

    @Test
    public void handler_normal_wild_twoLine_test() {
        IbongAdarnaSymbolDo[][] symbolArr = RewardTestMocker.mockSymbolArr_handler_normal_wild_twoLine_test();
        RewardResultDo<IbongAdarnaSymbolDo> resultDo = ibongAdarnaRewardHandler.handle(RewardTestMocker.buildTestRewardHandleDo(BetSubTypeEnum.NORMAL_BET, symbolArr, 100L));
        assertNotNull(resultDo);
        assertTrue(CollectionUtil.isNotEmpty(resultDo.getRewardLineDetailList()));
        assertEquals(2, resultDo.getRewardLineDetailList().size());
        assertEquals(IbongAdarnaSymbolDo.buildIbongSymbolDo(IbongAdarnaGameHelper.IBONG_ADARNA_GAME_INFO.getSymbolById(SymbolEnum.Symbol1001.getId()), SymbolBreakUpEnum.NO_BREAK_UP),
                resultDo.getRewardLineDetailList().get(0).getRewardSymbol());
        assertEquals(IbongAdarnaSymbolDo.buildIbongSymbolDo(IbongAdarnaGameHelper.IBONG_ADARNA_GAME_INFO.getSymbolById(SymbolEnum.Symbol1002.getId()), SymbolBreakUpEnum.NO_BREAK_UP),
                resultDo.getRewardLineDetailList().get(1).getRewardSymbol());
    }

    @Test
    public void handler_normal_allWild_test() {
        IbongAdarnaSymbolDo[][] symbolArr = RewardTestMocker.mockSymbolArr_handler_normal_allWild_test();
        RewardResultDo<IbongAdarnaSymbolDo> resultDo = ibongAdarnaRewardHandler.handle(RewardTestMocker.buildTestRewardHandleDo(BetSubTypeEnum.NORMAL_BET, symbolArr, 100L));
        assertNotNull(resultDo);
        assertTrue(CollectionUtil.isNotEmpty(resultDo.getRewardLineDetailList()));
        assertEquals(5, resultDo.getRewardLineDetailList().size());
        assertEquals(IbongAdarnaSymbolDo.buildIbongSymbolDo(IbongAdarnaGameHelper.IBONG_ADARNA_GAME_INFO.getSymbolById(SymbolEnum.Wild.getId()), SymbolBreakUpEnum.NO_BREAK_UP),
                resultDo.getRewardLineDetailList().get(0).getRewardSymbol());
    }

}