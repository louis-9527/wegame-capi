package com.wegame.capi.biz.module.bakunawa.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.BakunawaConfig;
import com.wegame.capi.biz.domain.biz.game.BakunawaGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static com.wegame.capi.biz.builder.RewardHandlerBuilder.build;
import static com.wegame.capi.biz.module.bakunawa.BakunawaGameHelper.BAKUNAWA_GAME_INFO;
import static com.wegame.capi.common.Constants.CONST_1000000;
import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
@Component("bakunawaGameNormalPlay")
public class BakunawaGameNormalPlay extends AbstractGame<BakunawaGameRecordDo> {

    private static final Integer DEFAULT_WHEEL_INDEX = 0;
    private static final GameInfoDo GAME_INFO = BAKUNAWA_GAME_INFO;
    @Autowired
    BakunawaConfig bakunawaConfig;
    @Autowired
    RewardHandler bakunawaRewardHandler;

    @Override
    public List<BakunawaGameRecordDo> play(GamePlayDo gamePlay) {
        List<BakunawaGameRecordDo> ret = new ArrayList<>();
        // 生成滚轮
        WheelDo<SymbolDo> wheel = generateNormalWheel();
        AssertUtil.assertNotNull(wheel, ErrorCode.ERR_INTER);
        // 中奖处理
        RewardResultDo<SymbolDo> rewardResult = bakunawaRewardHandler.handle(build(GAME_INFO, gamePlay, wheel, BigDecimal.ZERO));
        // 构建返回
        BakunawaGameRecordDo record = buildGameRecord(generateGameRecordId(),
                gamePlay.getBetRecordId(),
                wheel,
                rewardResult,
                rewardResult.getTotalRewardAmount());
        ret.add(record);
        return ret;
    }

    /**
     * 滚轮生成
     *
     * @return 滚轮
     */
    private WheelDo<SymbolDo> generateNormalWheel() {
        SymbolDo[][] symbolArr = new SymbolDo[GAME_INFO.getRow()][GAME_INFO.getColumn()];
        // 元素出现概率配置
        TreeMap<String, List<BigDecimal>> wheelProbabilisticConfigMap = bakunawaConfig.getProbabilisticConfig().getCommonPlayWheelProbabilisticConfig();
        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, GAME_INFO.getRow() * GAME_INFO.getColumn());
        int randomNumberIndex = 0;
        for (int column = ONE_COLUM_INDEX; column < GAME_INFO.getColumn(); column++) {
            // 列概率
            TreeMap<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(column, wheelProbabilisticConfigMap);
            Integer randomNumber = randomNumberList.get(randomNumberIndex);
            SymbolDo symbolDo = randomNormalSymbol(randomNumber, GAME_INFO, columProbabilisticMap);
            AssertUtil.assertNotNull(symbolDo, ErrorCode.ERR_INTER);
            symbolArr[ONE_ROW_INDEX][column] = symbolDo;
        }
        return WheelDo.<SymbolDo>builder()
                .index(DEFAULT_WHEEL_INDEX)
                .symbolArr(symbolArr)
                .build();
    }


    private static BakunawaGameRecordDo buildGameRecord(Long id,
                                                        Long betRecordId,
                                                        WheelDo<SymbolDo> wheel,
                                                        RewardResultDo<SymbolDo> rewardResult,
                                                        Long overallRewardAmount) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return BakunawaGameRecordDo.builder()
                .id(id)
                .betRecordId(betRecordId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .overallRewardAmount(overallRewardAmount)
                .build();
    }
}
