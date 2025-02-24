package com.wegame.capi.biz.module.ibongadarna.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.IbongAdarnaConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.IbongAdarnaGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.IbongSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.AmountUtil;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.SymbolBreakUpEnum;
import com.wegame.capi.common.enums.SymbolRepeatTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static com.wegame.capi.biz.module.ibongadarna.IbongAdarnaGameHelper.IBONG_ADARNA_GAME_INFO;
import static com.wegame.capi.common.Constants.CONST_10000;
import static com.wegame.capi.common.Constants.CONST_1000000;

@Slf4j
@Component
public class IbongAdarnaGame extends AbstractGame<IbongAdarnaGameRecordDo> {

    private static final Integer DEFAULT_WHEEL_INDEX = 0;

    @Autowired
    IbongAdarnaConfig ibongAdarnaConfig;
    @Autowired
    RewardHandler ibongAdarnaRewardHandler;


    @Override
    public GameInfoDo getGameInfo() {
        return IBONG_ADARNA_GAME_INFO;
    }

    @Override
    public List<IbongAdarnaGameRecordDo> play(GamePlayDo gamePlay) {
        List<IbongAdarnaGameRecordDo> ret = new ArrayList<>();
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        Map<String, List<BigDecimal>> wheelProbabilisticConfigMap = ibongAdarnaConfig.getProbabilisticConfig().getCommonPlayWheelProbabilisticConfig();
        WheelDo<IbongSymbolDo> wheel = generateNormalWheel(gameInfo, wheelProbabilisticConfigMap);

        AssertUtil.assertNotNull(wheel, ErrorCode.ERR_INTER);
        RewardResultDo<IbongSymbolDo> rewardResult = ibongAdarnaRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheel, BigDecimal.ZERO));
        IbongAdarnaGameRecordDo record = buildGameRecord(generateGameRecordId(),
                gamePlay.getBetRecordId(),
                wheel,
                rewardResult,
                AmountUtil.maxAmountControl(rewardResult.getTotalRewardAmount(), gamePlay.getBetAmount(),ibongAdarnaConfig.getMaxRewardMultiple()),
                AmountUtil.reachMaxMultiple(rewardResult.getTotalRewardAmount(), gamePlay.getBetAmount(),ibongAdarnaConfig.getMaxRewardMultiple()));
        ret.add(record);
        // 判断是否命中免费轮
        return ret;
    }

    /**
     * 正常滚轮生成：生成逻辑无任何限制
     *
     * @param gameInfo 游戏信息
     * @return 滚轮
     */
    private WheelDo<IbongSymbolDo> generateNormalWheel(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        IbongSymbolDo[][] symbolArr = new IbongSymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
        // 生成整盘随机数(row*column)
        for (int colum = 0; colum < gameInfo.getColumn(); colum++) {
            Map<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(colum, probabilisticConfig);
            // 每一列开始的时候决定是2个元素 or 1个元素
            SymbolRepeatTypeEnum symbolRepeatType = randomColumSymbolCount(colum);
            AssertUtil.assertNotNull(symbolRepeatType, ErrorCode.ERR_INTER, "invalid symbolRepeatType");
            generateColumSymbol(symbolArr, colum, gameInfo, this.preGenerateSymboDoList(symbolRepeatType, gameInfo, columProbabilisticMap));
        }
        return WheelDo.<IbongSymbolDo>builder()
                .index(DEFAULT_WHEEL_INDEX)
                .symbolArr(symbolArr).build();
    }


    /**
     * 每一列只有两个不同的元素
     *
     * @param symbolArr 元素数组
     * @param colum     当前列
     * @param gameInfo  游戏信息
     */
    private void generateColumSymbol(IbongSymbolDo[][] symbolArr, int colum, GameInfoDo gameInfo, List<SymbolDo> preSymbolGenList) {
        Map<String, Map<String, Integer>> symbolBreakUpProbabilisticMap = buildSymbolBreakUpProbabilisticMap();
        // 生成好固定个数
        int preSymbolIndex = 0;
        int preSymbolGenCount = preSymbolGenList.size();
        for (int row = 0; row < gameInfo.getRow(); row++) {
            SymbolDo symbolDo;
            if (preSymbolGenCount <= 1) {
                preSymbolIndex = 0;
            } else if (preSymbolIndex >= preSymbolGenCount) {
                preSymbolIndex = randomService.randomInt(0, preSymbolGenCount - 1);
            }
            symbolDo = preSymbolGenList.get(preSymbolIndex);
            // 分裂
            SymbolBreakUpEnum breakUpCount = randomSymbolBreakUpCount(symbolDo, symbolBreakUpProbabilisticMap);
            IbongSymbolDo ibongSymbolDo = IbongSymbolDo.buildIbongSymbolDo(symbolDo, breakUpCount);
            // 每一列元素个数控制,每一列最多只能有2个元素
            symbolArr[row][colum] = ibongSymbolDo;
            preSymbolIndex++;
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
    private List<SymbolDo> preGenerateSymboDoList(SymbolRepeatTypeEnum symbolRepeatType, GameInfoDo gameInfo, Map<String, Integer> columProbabilisticMap) {
        Set<SymbolDo> symbolDoSet = Sets.newHashSet();
        while (true) {
            if (symbolDoSet.size() < symbolRepeatType.getTimes()) {
                SymbolDo symbolDo = randomNormalSymbol(randomService.randomInt(1, CONST_1000000), gameInfo, columProbabilisticMap);
                symbolDoSet.add(symbolDo);
            } else {
                return Lists.newArrayList(symbolDoSet);
            }
        }
    }

    private SymbolRepeatTypeEnum randomColumSymbolCount(int colum) {
        Map<String, Integer> columSymbolRepeatProbabilisticMap = this.buildPerColumSymbolRepeatProbabilisticMap(colum);
        String repateType = null;
        int random = randomService.randomInt(1, CONST_1000000);
        for (Map.Entry<String, Integer> entry : columSymbolRepeatProbabilisticMap.entrySet()) {
            if (random <= entry.getValue()) {
                repateType = entry.getKey();
            }
        }
        return Optional.ofNullable(SymbolRepeatTypeEnum.valueOfCode(repateType)).orElse(SymbolRepeatTypeEnum.TWO);
    }

    /**
     * 随机计算元素分裂倍数
     *
     * @param symbolDo                      元素
     * @param symbolBreakUpProbabilisticMap 分裂概率配置map
     * @return 分裂倍数
     */
    private SymbolBreakUpEnum randomSymbolBreakUpCount(SymbolDo symbolDo, Map<String, Map<String, Integer>> symbolBreakUpProbabilisticMap) {
        int random = randomService.randomInt(1, CONST_1000000);
        Map<String, Integer> symbolBreakUpProbabilistic = symbolBreakUpProbabilisticMap.get(symbolDo.getId());
        String breakUpKey = null;
        for (Map.Entry<String, Integer> entry : symbolBreakUpProbabilistic.entrySet()) {
            if (random <= entry.getValue()) {
                breakUpKey = entry.getKey();
            }
        }
        return Optional.ofNullable(SymbolBreakUpEnum.valueOfCode(breakUpKey)).orElse(SymbolBreakUpEnum.NO_BREAK_UP);
    }

    /**
     * 构建每一列元素重复次数概率列表
     *
     * @return list
     */
    private Map<String, Integer> buildPerColumSymbolRepeatProbabilisticMap(int colum) {
        Map<String, BigDecimal> columSymbolRepeatProbabilisticConfigMap = ibongAdarnaConfig.getProbabilisticConfig()
                .getPerColumSymbolRepeatProbabilisticConfig().get(colum);
        Map<String, Integer> perColumSymbolRepeatProbabilisticMap = new HashMap<>();
        columSymbolRepeatProbabilisticConfigMap.forEach((key, probabilistic) ->
                perColumSymbolRepeatProbabilisticMap.put(key, probabilistic.multiply(new BigDecimal(CONST_10000)).intValue()));
        return perColumSymbolRepeatProbabilisticMap;
    }

    private Map<String, Map<String, Integer>> buildSymbolBreakUpProbabilisticMap() {
        Map<String, Map<String, Integer>> symbolBreakUpProbabilisticMap = new HashMap<>();

        Map<String, Map<String, BigDecimal>> symbolBreakUpProbabilisticConfig =
                ibongAdarnaConfig.getProbabilisticConfig().getSymbolBreakUpProbabilisticConfig();
        for (Map.Entry<String, Map<String, BigDecimal>> breakUpEnt : symbolBreakUpProbabilisticConfig.entrySet()) {
            Map<String, BigDecimal> valueMap = breakUpEnt.getValue();
            Map<String, Integer> intValueMap = new HashMap<>();
            valueMap.forEach((key, probabilistic) -> intValueMap.put(key, probabilistic.multiply(new BigDecimal(Constants.CONST_10000)).intValue()));
            symbolBreakUpProbabilisticMap.put(breakUpEnt.getKey(), intValueMap);
        }
        return symbolBreakUpProbabilisticMap;
    }

    private static IbongAdarnaGameRecordDo buildGameRecord(Long id,
                                                           Long betRecordId,
                                                           WheelDo<IbongSymbolDo> wheel,
                                                           RewardResultDo<IbongSymbolDo> rewardResult,
                                                           Long overallRewardAmount,
                                                           Boolean accumulateReachMaxMultiple) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return IbongAdarnaGameRecordDo.builder()
                .id(id)
                .betRecordId(betRecordId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .overallRewardAmount(overallRewardAmount)
                .accumulateReachMaxMultiple(accumulateReachMaxMultiple)
                .build();
    }
}
