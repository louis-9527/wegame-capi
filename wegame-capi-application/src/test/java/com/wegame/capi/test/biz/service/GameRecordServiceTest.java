package com.wegame.capi.test.biz.service;

import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GameRecordServiceTest extends BaseTest {
    @Autowired
    GameRecordService<MariaMakilingGameRecordDo> gameRecordService;

    @Test
    public void test_add(){
        long curr = System.currentTimeMillis();
        for (long i = curr; i < curr + 10; i++) {
            List<MariaMakilingGameRecordDo> gameRecordList = new ArrayList<>();
            gameRecordList.add(MariaMakilingGameRecordDo.builder()
                    .id(i)
                    .playerId("123456")
                    .betRecordId(1887415334551220224L)
                    .build());
            gameRecordService.batchAdd(gameRecordList, GameTypeEnum.MARIA_MAKILING, MariaMakilingGameRecordDo.class);
        }
    }
}
