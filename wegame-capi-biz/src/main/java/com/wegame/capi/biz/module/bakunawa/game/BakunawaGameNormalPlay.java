package com.wegame.capi.biz.module.bakunawa.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
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
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.SymbolBreakUpEnum;
import com.wegame.capi.common.enums.SymbolRepeatTypeEnum;
import com.wegame.capi.common.utils.GameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static com.wegame.capi.biz.module.ibongadarna.IbongAdarnaGameHelper.IBONG_ADARNA_GAME_INFO;
import static com.wegame.capi.common.Constants.CONST_10000;
import static com.wegame.capi.common.Constants.CONST_1000000;

@Slf4j
@Component("bakunawaGameNormalPlay")
public class BakunawaGameNormalPlay extends AbstractGame<BakunawaGameRecordDo> {

    private static final Integer DEFAULT_WHEEL_INDEX = 0;
    private static final GameInfoDo gameInfo = IBONG_ADARNA_GAME_INFO;
    @Autowired // todo @maidong
    IbongAdarnaConfig ibongAdarnaConfig;
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
        TreeMap<String, List<BigDecimal>> wheelProbabilisticConfigMap = ibongAdarnaConfig.getProbabilisticConfig().getCommonPlayWheelProbabilisticConfig();
        // 逐个生成滚轮
        for (int colum = 0; colum < gameInfo.getColumn(); colum++) {
            TreeMap<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(colum, wheelProbabilisticConfigMap);
            // 每一列开始的时候决定是2个元素 or 1个元素
            SymbolRepeatTypeEnum symbolRepeatType = randomSymbolRepeatType(colum);
            AssertUtil.assertNotNull(symbolRepeatType, ErrorCode.ERR_INTER, "invalid symbolRepeatType");
            generateColumSymbol(symbolArr, colum, gameInfo, this.preGenerateSymboDoList(symbolRepeatType, gameInfo, columProbabilisticMap));
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
     * @param preSymbolGenList 预生成的当前列元素集合
     */
    private void generateColumSymbol(SymbolDo[][] symbolArr, int colum, GameInfoDo gameInfo, List<SymbolDo> preSymbolGenList) {
        TreeMap<String, TreeMap<String, Integer>> symbolBreakUpProbabilisticMap = buildSymbolBreakUpProbabilisticMap();
        int preSymbolGenCount = preSymbolGenList.size();
        AssertUtil.assertTrue(preSymbolGenCount > 0 && preSymbolGenCount <= 2, ErrorCode.ERR_INTER, "invalid preSymbolGenCount");
        int[] rowIndex = new int[gameInfo.getRow()];
        for (int row = 0; row < gameInfo.getRow(); row++) {
            SymbolDo symbolDo;
            if (preSymbolGenCount == 1) {
                // 一列只有一个元素
                symbolDo = preSymbolGenList.get(0);
            } else {
                if (row == 2) {
                    // 第三行：如果第一行和第二行一样，则取不一样的元素
                    if (rowIndex[row - 1] == rowIndex[row - 2]) {
                        int preSymbolIndex = rowIndex[row - 1] == 0 ? 1 : 0;
                        symbolDo = preSymbolGenList.get(preSymbolIndex);
                    } else {
                        // 否则：取第二行的元素
                        symbolDo = preSymbolGenList.get(rowIndex[row - 1]);
                    }
                } else {
                    // 第一行随机
                    // 第二行随机
                    int preSymbolIndex = randomService.randomInt(0, preSymbolGenCount - 1);
                    symbolDo = preSymbolGenList.get(preSymbolIndex);
                    rowIndex[row] = preSymbolIndex;
                }
            }
            // 分裂
            SymbolBreakUpEnum breakUpCount = randomSymbolBreakUpType(symbolDo, symbolBreakUpProbabilisticMap);
            IbongAdarnaSymbolDo ibongSymbolDo = IbongAdarnaSymbolDo.buildIbongSymbolDo(symbolDo, breakUpCount);
            symbolArr[row][colum] = ibongSymbolDo;
        }
    }

    /**
     * 预生成固定个数不重复的基础元素
     *
     * @param symbolRepeatType      元素重复类型
     * @param gameInfo              游戏信息
     * @param columProbabilisticMap 元素出现概率
     * @return 不重复的元素集合
     */
    private List<SymbolDo> preGenerateSymboDoList(SymbolRepeatTypeEnum symbolRepeatType, GameInfoDo gameInfo, TreeMap<String, Integer> columProbabilisticMap) {
        Set<SymbolDo> symbolDoSet = Sets.newHashSet();
        while (symbolDoSet.size() < symbolRepeatType.getTimes()) {
            SymbolDo symbolDo = randomNormalSymbol(randomService.randomInt(1, CONST_1000000), gameInfo, columProbabilisticMap);
            symbolDoSet.add(symbolDo);
        }
        return Lists.newArrayList(symbolDoSet);
    }

    private SymbolRepeatTypeEnum randomSymbolRepeatType(int colum) {
        TreeMap<String, Integer> columSymbolRepeatProbabilisticMap = this.buildPerColumSymbolRepeatProbabilisticMap(colum);
        String repeatType = GameUtil.randomKey(columSymbolRepeatProbabilisticMap, randomService.randomInt(1, CONST_1000000));
        return Optional.ofNullable(SymbolRepeatTypeEnum.valueOfCode(repeatType)).orElse(SymbolRepeatTypeEnum.TWO);
    }

    /**
     * 随机计算元素分裂倍数
     *
     * @param symbolDo                      元素
     * @param symbolBreakUpProbabilisticMap 分裂概率配置map
     * @return 分裂倍数
     */
    private SymbolBreakUpEnum randomSymbolBreakUpType(SymbolDo symbolDo, TreeMap<String, TreeMap<String, Integer>> symbolBreakUpProbabilisticMap) {
        TreeMap<String, Integer> symbolBreakUpProbabilistic = symbolBreakUpProbabilisticMap.get(symbolDo.getId());
        String breakUpKey = GameUtil.randomKey(symbolBreakUpProbabilistic, randomService.randomInt(1, CONST_1000000));
        return Optional.ofNullable(SymbolBreakUpEnum.valueOfCode(breakUpKey)).orElse(SymbolBreakUpEnum.NO_BREAK_UP);
    }

    /**
     * 构建每一列元素重复次数概率列表
     *
     * @return list
     */
    private TreeMap<String, Integer> buildPerColumSymbolRepeatProbabilisticMap(int colum) {
        TreeMap<String, BigDecimal> columSymbolRepeatProbabilisticConfigMap = ibongAdarnaConfig.getProbabilisticConfig()
                .getPerColumSymbolRepeatProbabilisticConfig().get(colum);
        TreeMap<String, Integer> perColumSymbolRepeatProbabilisticMap = new TreeMap<>();
        columSymbolRepeatProbabilisticConfigMap.forEach((key, probabilistic) ->
                perColumSymbolRepeatProbabilisticMap.put(key, probabilistic.multiply(new BigDecimal(CONST_10000)).intValue()));
        return perColumSymbolRepeatProbabilisticMap;
    }

    private TreeMap<String, TreeMap<String, Integer>> buildSymbolBreakUpProbabilisticMap() {
        TreeMap<String, TreeMap<String, Integer>> symbolBreakUpProbabilisticMap = new TreeMap<>();
        TreeMap<String, TreeMap<String, BigDecimal>> symbolBreakUpProbabilisticConfig =
                ibongAdarnaConfig.getProbabilisticConfig().getSymbolBreakUpProbabilisticConfig();
        for (Map.Entry<String, TreeMap<String, BigDecimal>> breakUpEnt : symbolBreakUpProbabilisticConfig.entrySet()) {
            Map<String, BigDecimal> valueMap = breakUpEnt.getValue();
            TreeMap<String, Integer> intValueMap = new TreeMap<>();
            valueMap.forEach((key, probabilistic) -> intValueMap.put(key, probabilistic.multiply(new BigDecimal(Constants.CONST_10000)).intValue()));
            symbolBreakUpProbabilisticMap.put(breakUpEnt.getKey(), intValueMap);
        }
        return symbolBreakUpProbabilisticMap;
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
