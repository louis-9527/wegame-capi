//package com.wegame.capi.biz.module.bakunawa.odds;
//
//import com.wegame.capi.biz.config.domain.BakunawaConfig;
//import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
//import com.wegame.capi.biz.domain.biz.odds.OddsCalculateDo;
//import com.wegame.capi.biz.domain.biz.symbol.IbongAdarnaSymbolDo;
//import com.wegame.capi.biz.module.base.odds.AbstractOddsCalculateStrategy;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.TreeMap;
//
//@Slf4j
//@Component("bakunawaOddsCalculateStrategy")
//public class BakunawaOddsCalculateStrategy
//        extends AbstractOddsCalculateStrategy<IbongAdarnaSymbolDo> {
//
//    @Autowired // todo @maidong
//    BakunawaConfig bakunawaConfig;
//
//    @Override
//    public TreeMap<String, TreeMap<String, BigDecimal>> getOddsConfig(OddsCalculateDo<IbongAdarnaSymbolDo> oddsCalculateDo) {
//        return bakunawaConfig.getOddsConfig().getCommonOddsConfig();
//    }
//}
