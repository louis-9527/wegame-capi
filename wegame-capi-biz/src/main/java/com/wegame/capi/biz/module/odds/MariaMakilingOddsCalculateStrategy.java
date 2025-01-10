package com.wegame.capi.biz.module.odds;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
import com.wegame.capi.common.enums.OddsTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

import static com.wegame.capi.biz.helper.MariaMakilingGameHelper.MARIA_MAKILING_COMMON_ODDS_CONFIG;
import static com.wegame.capi.biz.helper.MariaMakilingGameHelper.MARIA_MAKILING_GAME_INFO;

@Slf4j
@Component("mariaMakilingOddsCalculateStrategy")
public class MariaMakilingOddsCalculateStrategy extends AbstractOddsCalculateStrategy implements InitializingBean {

    @Override
    public Map<SymbolEnum, Map<OddsTypeEnum, BigDecimal>> getOddsConfig(OddsCalculateDo oddsCalculateDo) {
        return MARIA_MAKILING_COMMON_ODDS_CONFIG;
    }

    @Override
    public void afterPropertiesSet() {
        // 校验-赔率配置
        AssertUtil.assertNotNull(MARIA_MAKILING_COMMON_ODDS_CONFIG, ErrorCode.ERR_INTER, "Odds Config Err");
        MARIA_MAKILING_GAME_INFO.getSymbolMap()
                .keySet()
                .forEach(symbolId -> {
                    Map<OddsTypeEnum, BigDecimal> map = MARIA_MAKILING_COMMON_ODDS_CONFIG.get(SymbolEnum.valueOfId(symbolId));
                    AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER, "Odds Config Err");
                });
    }
}
