package com.wegame.capi.test.biz.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.MariaMakilingBetRecordDetailDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class BetRecordServiceTest extends BaseTest {
    @Autowired
    BetRecordService betRecordService;

    //    @Test
    //    public void test_get(){
    //        BetRecordDo<Object> igoslot01 = betRecordService.get(1887415334551220224L, "igoslot01", GameTypeEnum.MARIA_MAKILING);
    //        log.info("igoslot01:{}", igoslot01);
    //        BetRecordDo<MariaMakilingBetRecordDetailDo> igoslot0100 = betRecordService.get(1887415334551220224L, "igoslot01", GameTypeEnum.MARIA_MAKILING);
    //        log.info("igoslot0100:{}", igoslot0100);
    //        BetRecordDo<MariaMakilingBetRecordDetailDo> igoslot0102 = betRecordService.get(1887415334551220224L, "igoslot01", GameTypeEnum.MARIA_MAKILING);
    //        log.info("igoslot0102:{}", igoslot0102);
    //    }

    @Test
    public void test_add(){
        BetRecordDo betRecordDo = BetRecordDo.builder()
                .id(1887415334551220224L)
                .playerId("123456")
                .betAmount(100L)
                .build();
        Long id = betRecordService.add(betRecordDo, GameTypeEnum.MARIA_MAKILING);
        Assert.assertNotNull(id);
    }

    public static void main(String[] args) {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        list.add("" + 0);
        list.add("" + 1);
        list.add("" + 2);
        int num = 3;
        while (!list.isEmpty()) {
            String val = list.remove(0);
            System.out.println("val: " + val);
            boolean ret = list.add("" + num ++);
            System.out.println("ret: " + ret);
        }
    }

    @Test
    public void test_listNotExistIdList(){
//        Set<Long> notExistIdList = betRecordService.listNotExistIdList(Sets.newHashSet(1890324803643088836L,1890324803643088896L, 1890324820269309952L), GameTypeEnum.MALAKAS);
//        log.info("test_listNotExistIdList:{}",notExistIdList);
    }
}
