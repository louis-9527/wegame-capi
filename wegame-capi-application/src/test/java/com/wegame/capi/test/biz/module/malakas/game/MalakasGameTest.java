package com.wegame.capi.test.biz.module.malakas.game;//package com.wegame.capi.test.biz.module.game;

import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.module.malakas.MalakasSymbolUtil;
import com.wegame.capi.biz.module.malakas.game.MalakasGame;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Slf4j
public class MalakasGameTest extends BaseTest {

    @Autowired
    MalakasGame malakasGame;

    @Test
    public void test_play() {
        GamePlayDo playDo = GamePlayDo.builder()
                .gameType(GameTypeEnum.MARIA_MAKILING)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .betAmount(100L)
                .build();
        for (int i = 0; i < 100; i++) {
            List<MalakasGameRecordDo> recordList = malakasGame.play(playDo);
            if (recordList.size() > 1) {
                recordList.forEach(item -> {
                    MalakasSymbolUtil.print(item.getWheel().getSymbolArr(), "test");
                });
            }
            System.out.println("end");
        }
    }
}