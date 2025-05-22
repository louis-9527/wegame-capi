package com.wegame.capi.biz.module.bakunawa.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.BakunawaConfig;
import com.wegame.capi.biz.domain.biz.game.BakunawaGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.IbongAdarnaSymbolDo;
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

import static com.wegame.capi.biz.module.bakunawa.BakunawaGameHelper.BAKUNAWA_GAME_INFO;

@Slf4j
@Component("bakunawaGameNormalPlay")
public class BakunawaGameNormalPlay extends AbstractGame<BakunawaGameRecordDo> {

    private static final Integer DEFAULT_WHEEL_INDEX = 0;
    private static final GameInfoDo gameInfo = BAKUNAWA_GAME_INFO;
    @Autowired
    BakunawaConfig bakunawaConfig;
    @Autowired
    RewardHandler bakunawaRewardHandler;

    @Override
    public List<BakunawaGameRecordDo> play(GamePlayDo gamePlay) {
        List<BakunawaGameRecordDo> ret = new ArrayList<>();
        // 生成滚轮
        WheelDo<SymbolDo> wheel = generateNormalWheel(gameInfo);
        AssertUtil.assertNotNull(wheel, ErrorCode.ERR_INTER);
        // 中奖处理
        RewardResultDo<SymbolDo> rewardResult = bakunawaRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheel, BigDecimal.ZERO));
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
     * 正常滚轮生成：生成逻辑无任何限制
     *
     * @param gameInfo 游戏信息
     * @return 滚轮
     */
    private WheelDo<SymbolDo> generateNormalWheel(GameInfoDo gameInfo) {
        SymbolDo[][] symbolArr = new SymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
        // 元素出现概率配置
        TreeMap<String, List<BigDecimal>> wheelProbabilisticConfigMap = bakunawaConfig.getProbabilisticConfig().getCommonPlayWheelProbabilisticConfig();
        // 逐个生成滚轮
        for (int colum = 0; colum < gameInfo.getColumn(); colum++) {
            // 列概率配置
            TreeMap<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(colum, wheelProbabilisticConfigMap);
            generateColumSymbol(symbolArr, colum, gameInfo, columProbabilisticMap);
        }
        return WheelDo.<SymbolDo>builder()
                .index(DEFAULT_WHEEL_INDEX)
                .symbolArr(symbolArr)
                .build();
    }

    /**
     * 每一列只有两个不同的元素
     *
     * @param symbolArr        元素数组
     * @param colum            当前列
     * @param gameInfo         游戏信息
     */
    private void generateColumSymbol(IbongAdarnaSymbolDo[][] symbolArr, int colum, GameInfoDo gameInfo, TreeMap<String, Integer> columProbabilisticMap) {
        TreeMap<String, TreeMap<String, Integer>> symbolBreakUpProbabilisticMap = buildSymbolBreakUpProbabilisticMap();
        int preSymbolGenCount = preSymbolGenList.size();
        AssertUtil.assertTrue(preSymbolGenCount > 0 && preSymbolGenCount <= 2, ErrorCode.ERR_INTER, "invalid preSymbolGenCount");
        int[] rowIndex = new int[gameInfo.getRow()];

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
