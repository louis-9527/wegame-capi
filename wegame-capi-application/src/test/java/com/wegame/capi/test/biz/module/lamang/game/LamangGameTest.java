package com.wegame.capi.test.biz.module.lamang.game;//package com.wegame.capi.test.biz.module.game;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.LamangGameRecordDo;
import com.wegame.capi.biz.domain.biz.symbol.LamangSymbolDo;
import com.wegame.capi.biz.module.lamang.LamangSymbolUtil;
import com.wegame.capi.biz.module.lamang.game.LamangGameNormalPlay;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wegame.capi.common.constants.GameConstants.*;

@Slf4j
public class LamangGameTest extends BaseTest {

    @Autowired
    LamangGameNormalPlay normalPlay;

    @Before
    public void beforeTest() {
        ThreadMdcUtil.setTraceIdIfAbsent();
        UserInfo userInfo = UserInfo.builder()
                .id("1001")
                .pId("50")
                .channelId("1050")
                .gameId(GameTypeEnum.LAM_ANG.getId())
                .playerId(TEST_PLAYER_ID)
                .playerName(TEST_PLAYER_NAME)
                .userName(TEST_USER_NAME)
                .nickName(TEST_NICK_NAME)
                .userLevel(TEST_USER_LEVEL)
                .token(TEST_TOKEN)
                .build();
        UserInfoContext.setUserInfo(userInfo);
        OperatorContext.setOperator(Operator.builder()
                .name(userInfo.getUserName())
                .userId(Long.valueOf(userInfo.getId()))
                .playerId(userInfo.getPlayerId())
                .build());
    }

    @Test
    public void test_normal_bet() {
        GamePlayDo playDo = GamePlayDo.builder()
                .gameType(GameTypeEnum.MALAKAS)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .betAmount(100L)
                .build();
        List<LamangGameRecordDo> recordList = normalPlay.play(playDo);
        recordList.forEach(item -> {
            LamangSymbolUtil.print(item.getWheel().getSymbolArr(), "test");
        });
    }

    @Test
    public void test_buy_bet() {
        GamePlayDo playDo = GamePlayDo.builder()
                .gameType(GameTypeEnum.LAM_ANG)
                .betType(BetTypeEnum.BUY_BET)
                .betSubType(BetSubTypeEnum.BUY_BET)
                .betAmount(100L)
                .payAmount(10000L)
                .build();
        List<LamangGameRecordDo> recordList = normalPlay.play(playDo);
        // 校验
        //// 1.是否为空
        Assert.assertNotNull(recordList);
        //// 2.校验数据条数（3个scatter 10轮 + 1 初始轮）
        Assert.assertTrue(recordList.size() > 10);

        LamangGameRecordDo firstWheelFirstGameRecord = recordList.get(0);
        //// 3.校验首轮总scatter数量 ==3
        Assert.assertTrue(countScatterInWheel(firstWheelFirstGameRecord.getWheel().getSymbolArr()) == 3);

        //// 4.校验整体数据scatter个数
        recordList.forEach(gameRecord -> validateScatter(gameRecord.getWheel().getSymbolArr()));


        //// 首轮parentId为0
        Assert.assertTrue(Objects.equals(firstWheelFirstGameRecord.getParentId(), 0L));
        List<LamangGameRecordDo> firstWheelGameRecordList = recordList.stream()
                .filter(item -> Objects.equals(item.getBatchId(), firstWheelFirstGameRecord.getId()))
                .sorted(Comparator.comparing(LamangGameRecordDo::getIndex))
                .collect(Collectors.toList());
        LamangGameRecordDo firstWheelLastGameRecord = firstWheelGameRecordList.get(firstWheelGameRecordList.size() - 1);
        LamangSymbolDo[][] symbolArr = firstWheelLastGameRecord.getWheel().getSymbolArr();
        validScatterNumberInColumn(symbolArr);

        // 打印
        recordList.forEach(item -> LamangSymbolUtil.print(item.getWheel().getSymbolArr(), "test"));
    }

    private void validateScatter(LamangSymbolDo[][] symbolArr) {
        validScatterNumberInWheel(symbolArr);
        validScatterNumberInColumn(symbolArr);
    }

    private void validScatterNumberInColumn(LamangSymbolDo[][] symbolArr) {
        for (int column = ONE_COLUM_INDEX; column < FIVE_COLUM_INDEX; column++) {
            // 一列最多包含1个scatter
            int scatterNumberInColumn = LamangSymbolUtil.countScatterInColumn(symbolArr, column);
            Assert.assertTrue(scatterNumberInColumn <= 1);
        }
    }

    private void validScatterNumberInWheel(LamangSymbolDo[][] symbolArr) {
        Assert.assertTrue(LamangSymbolUtil.countScatterInWheel(symbolArr) <= 3);
    }

    private int countScatterInWheel(LamangSymbolDo[][] symbolArr) {
        return LamangSymbolUtil.countScatterInWheel(symbolArr);
    }
}