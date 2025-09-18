package com.wegame.capi.test.biz.innerdos.decider;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.metrics.MetricsCacheKeyDo;
import com.wegame.capi.biz.innerdos.decider.InnerDosDecideResult;
import com.wegame.capi.biz.innerdos.decider.InnerDosDeciderManager;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

@Slf4j
public class InnerDosDeciderManagerTest extends BaseTest {

    @Autowired
    private InnerDosDeciderManager innerDosDeciderManager;

    @Test
    public void decide_normal_test() {
        GamePlayDo gamePlayDo = GamePlayDo.builder()
                .gamePlayType(GamePlayTypeEnum.TEMPLATE_PLAY)
                .gameType(GameTypeEnum.IBONG_ADARNA)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .betAmount(1000L)
                .payAmount(1000L)
                .build();
        UserInfoContext.setUserInfo(UserInfo.builder()
                .pId("50")
                .channelId("1050")
                .playerId("test001")
                .playerName("test001")
                .gameId(GameTypeEnum.IBONG_ADARNA.getId())
                .ut("aff")
                .build());
        InnerDosDecideResult decideResult = innerDosDeciderManager.decide(GameTypeEnum.IBONG_ADARNA, gamePlayDo);
        assertNotNull(decideResult);
        assertTrue(StrUtil.isNotEmpty(decideResult.getTemplateIdRange()));
        log.info("decideResult result:{}", JSON.toJSONString(decideResult));
    }


}
