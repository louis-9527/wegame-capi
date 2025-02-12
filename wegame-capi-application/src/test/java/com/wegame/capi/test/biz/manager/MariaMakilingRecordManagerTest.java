package com.wegame.capi.test.biz.manager;

import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.manager.MariaMakilingRecordManager;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.List;

@Slf4j
public class MariaMakilingRecordManagerTest extends BaseTest {

    @Autowired
    MariaMakilingRecordManager mariaMakilingRecordManager;

    @BeforeAll
    public static void beforeTest() {
        UserInfoContext.setUserInfo(UserInfo.builder()
                .id("10000")
                .build());
    }

    @Test
    public void test_listGameRecordWithCache() {
        for (int i = 0; i < 10; i++) {
            GameRecordQuery query = GameRecordQuery.builder()
                    .betRecordId(1887415334551220224L)
                    .playerId("123456")
                    .orderByClause("_id")
                    .direction(Sort.Direction.ASC)
                    .cacheKey(String.format("%s_%s", 1887415334551220224L, "123456"))
                    .build();
            List<MariaMakilingGameRecordDo> makilingGameRecordDoList = mariaMakilingRecordManager.listGameRecordWithCache(query, GameTypeEnum.MARIA_MAKILING);
            log.info("makilingGameRecordDoList: {}", makilingGameRecordDoList.size());
        }
    }
}
