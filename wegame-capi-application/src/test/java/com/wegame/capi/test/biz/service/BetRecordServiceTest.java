package com.wegame.capi.test.biz.service;

import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MariaMakilingBetRecordDetailDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.CacheInterceptor;

@Slf4j
public class BetRecordServiceTest extends BaseTest {
    @Autowired
    BetRecordService betRecordService;

    @Test
    public void test_get() {
        for (int i = 1; i <= 100; i++) {
            try {
                BetRecordDo<MariaMakilingBetRecordDetailDo> igoslot0100 = betRecordService.get(1887425258517020672L, "cptestjb100001", GameTypeEnum.MARIA_MAKILING);
            } catch (Exception e) {
                log.error("errMsg: {}", e.getMessage(), e);
            }
        }
        for (int i = 1; i <= 100; i++) {
            BetRecordDo<MariaMakilingBetRecordDetailDo> igoslot0102 = betRecordService.get(1887458246424379392L, "slottest01", GameTypeEnum.MARIA_MAKILING);
        }
        System.out.println();
    }
}
