package com.wegame.capi.test.application.controller.mariamakiling;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.capi.client.domain.request.query.PageQueryBetRecordRequest;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.test.application.controller.BetRecordControllerTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

@Slf4j
public class MariamakilingBetRecordControllerTest extends BetRecordControllerTest {

    @BeforeAll
    public static void beforeTest() {
        UserInfoContext.setUserInfo(UserInfo.builder()
                .id("10000")
                .userName("system")
                .gameId(GameTypeEnum.MARIA_MAKILING.getId())
                .build());
        OperatorContext.setOperator(Operator.builder()
                .name("system")
                .userId(10000L)
                .playerId("10000")
                .build());
    }

    @Test
    public void test_pageQuery() {
        beforeTest();
        commonPageQuery();
    }

    @Test
    public void test_pageGet() {
        beforeTest();
        // todo
        //        commonGet("12345");
    }
}
