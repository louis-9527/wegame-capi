package com.wegame.capi.test.biz.module.diwatamayari.odds;

import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.module.diawatamayari.DiwataMayariGameHelper;
import com.wegame.capi.biz.module.diawatamayari.odds.DiwataMayariOddsCalculateStrategy;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.OddsTypeEnum;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class DiwataMayariOddsCalculateStrategyTest extends BaseTest {

    @Autowired
    DiwataMayariOddsCalculateStrategy diwataMayariOddsCalculateStrategy;

    @Test
    public void calculateOdds_normal_test() {
        // 当前游戏所有可参与赔率计算的元素列表获取
        GameInfoDo gameInfo = DiwataMayariGameHelper.DIWATA_MAYARI_GAME_INFO;
        Map<String, SymbolDo> symbolMap = gameInfo.getSymbolMap();
        List<SymbolDo> allCanOddsSymbolList = symbolMap.values().stream().filter(SymbolDo::isCommonSymbol).collect(Collectors.toList());
        // 赔率配置
        TreeMap<String, TreeMap<String, BigDecimal>> oddsConfig = diwataMayariOddsCalculateStrategy.getOddsConfig(null);
        // 每一个元素的赔率验证
        for (SymbolDo symbolDo : allCanOddsSymbolList) {
            // 当前元素的赔率配置
            TreeMap<String, BigDecimal> symbolOddsMapConfig = oddsConfig.get(symbolDo.getId());
            // 单个元素的每个连线赔率验证
            for (OddsTypeEnum oddsType : OddsTypeEnum.values()) {
                BigDecimal bigDecimal = diwataMayariOddsCalculateStrategy.calculateOdds(OddsCalculateDo.<DiwataMayariSymbolDo>builder()
                        .gameType(GameTypeEnum.DIWATA_MAYARI)
                        .betType(BetTypeEnum.NORMAL_BET)
                        .betSubType(BetSubTypeEnum.NORMAL_BET)
                        .oddsType(oddsType)
                        .symbol(DiwataMayariSymbolDo.builder().id(symbolDo.getId()).name(symbolDo.getName()).symbolType(symbolDo.getSymbolType()).build())
                        .build());
                assertNotNull(bigDecimal);
                assertEquals(symbolOddsMapConfig.get(oddsType.getCode()), bigDecimal);
            }
        }
    }


}
