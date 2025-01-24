package com.wegame.capi.biz.module.mariamakiling.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.RewardHandlerBuilder.build;
import static com.wegame.capi.biz.module.mariamakiling.MariaMakilingGameHelper.MARIA_MAKILING_GAME_INFO;
import static com.wegame.capi.biz.module.mariamakiling.MariaMakilingGameHelper.MARIA_MAKILING_SCATTER;
import static com.wegame.capi.common.Constants.CONST_1000000;
import static com.wegame.capi.common.constants.GameConstants.*;

@Slf4j
@Component
public class MariaMakilingGame extends AbstractGame<MariaMakilingGameRecordDo> {

    // ONE_COLUM_INDEX,THREE_COLUM_INDEX,FIVE_COLUM_INDEX顺序不可更改
    private static final List<Integer> SCATTER_COLUM_INDEX_LIST = Arrays.asList(
            ONE_COLUM_INDEX,
            THREE_COLUM_INDEX,
            FIVE_COLUM_INDEX);
    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    @Autowired
    MariaMakilingConfig mariaMakilingConfig;
    @Autowired
    RewardHandler mariaMakilingRewardHandler;

    @Override
    public GameInfoDo getGameInfo() {
        return MARIA_MAKILING_GAME_INFO;
    }

    @Override
    public List<MariaMakilingGameRecordDo> play(GamePlayDo gamePlay) {
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        // 生成-游戏滚轮列表
        List<WheelDo> wheelList = generate(gamePlay);
        // 组装&返回-游戏记录列表
        return Optional.ofNullable(wheelList)
                .orElse(Collections.emptyList())
                .stream()
                .map(wheel -> buildGameRecord(wheel, mariaMakilingRewardHandler.handle(build(gameInfo, gamePlay, wheel))))
                .collect(Collectors.toList());
    }

    private List<WheelDo> generate(GamePlayDo gamePlay) {
        GameInfoDo gameInfo = getGameInfo();
        Map<String, List<BigDecimal>> probabilisticConfig = getProbabilisticConfig(gamePlay);
        if (gamePlay.isFreeSpinsFlag()) {
            return generateMultiWheel(gameInfo, probabilisticConfig);
        } else {
            // 根据投注类型：生成游戏滚轮
            switch (gamePlay.getBetType()) {
                case NORMAL_BET:
                    return Collections.singletonList(generateNormalWheel(gameInfo, probabilisticConfig));
                case BUY_BET:
                    return Collections.singletonList(generateFreeSpinsWheel(gameInfo, probabilisticConfig));
                default:
                    throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
            }
        }
    }

    private MariaMakilingGameRecordDo buildGameRecord(WheelDo wheel, RewardResultDo rewardResult) {
        return MariaMakilingGameRecordDo.builder()
                .index(wheel.getIndex())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .build();
    }

    /**
     * 正常滚轮生成：生成逻辑无任何限制
     *
     * @param gameInfo 游戏信息
     * @return 滚轮
     */
    private WheelDo generateNormalWheel(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        SymbolDo[][] symbolArr = new SymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, gameInfo.getRow() * gameInfo.getColumn());
        int randomNumberindex = 0;
        for (int colum = 0; colum < gameInfo.getColumn(); colum++) {
            // 一列只能包含一个scatter
            boolean hasScatter = false;
            Map<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(colum, probabilisticConfig);
            for (int row = 0; row < gameInfo.getRow(); row++) {
                Integer random = randomNumberList.get(randomNumberindex);
                if (hasScatter) {
                    // 生成非scatter
                    SymbolDo symbolDo = randomNonScatterSymbol(random, gameInfo, columProbabilisticMap);
                    symbolArr[row][colum] = symbolDo;
                } else {
                    // 随机生成
                    SymbolDo symbolDo = randomNormalSymbol(random, gameInfo, columProbabilisticMap);
                    symbolArr[row][colum] = symbolDo;
                    hasScatter = symbolDo.isScatterSymbol();
                }
                randomNumberindex++;
            }
        }
        return WheelDo.builder()
                .index(WHEEL_DEFAULT_INDEX)
                .symbolArr(symbolArr)
                .build();
    }

    /**
     * 免费滚轮生成：中奖Scatter
     *
     * @param gameInfo 游戏信息
     * @return 滚轮
     */
    private WheelDo generateFreeSpinsWheel(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        WheelDo wheelDo = generateNormalWheel(gameInfo, probabilisticConfig);
        SymbolDo[][] symbolArr = wheelDo.getSymbolArr();
        // 生成整盘随机数(SCATTER_COLUM_INDEX_LIST.size())个
        List<Integer> randomNumberList = randomService.randomIntList(0, gameInfo.getRow() - 1, SCATTER_COLUM_INDEX_LIST.size());
        int randomNumberIndex = 0;
        // 检查1，3，5列是否有Scatter，如果不存在，则1，3，5列各自随机一行的的元素设置为scatter
        for (Integer colum : SCATTER_COLUM_INDEX_LIST) {
            boolean hasScatter = false;
            for (int i = 0; i < gameInfo.getRow(); i++) {
                SymbolDo symbol = symbolArr[i][colum];
                if (symbol.isScatterSymbol()) {
                    hasScatter = true;
                    break;
                }
            }
            if (!hasScatter) {
                // 生成一个0到行之间的随机整数
                int randomRow = randomNumberList.get(randomNumberIndex++);
                symbolArr[randomRow][colum] = MARIA_MAKILING_SCATTER;
            }
        }
        return WheelDo.builder()
                .index(WHEEL_DEFAULT_INDEX)
                .symbolArr(symbolArr)
                .build();
    }

    /**
     * 生成多个滚轮
     * 1.无 Scatter
     * 2.wild需要保留
     *
     * @param gameInfo 游戏信息
     * @return 滚轮列表
     */
    private List<WheelDo> generateMultiWheel(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        // 保存全局wild
        SymbolDo[][] wildSymbolArr = new SymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
        // 免费次数
        int wheelNumber = getWheelNumber();
        // 生成轮盘总和
        List<WheelDo> wheels = new ArrayList<>();
        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, wheelNumber * gameInfo.getRow() * gameInfo.getColumn());
        int randomNumberIndex = 0;
        for (int number = 1; number <= wheelNumber; number++) {
            SymbolDo[][] symbolArr = new SymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
            for (int colum = 0; colum < gameInfo.getColumn(); colum++) {
                // 当前列元素概率
                Map<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(colum, probabilisticConfig);
                for (int row = 0; row < gameInfo.getRow(); row++) {
                    if (wildSymbolArr[row][colum] != null) {
                        // 当前坐标对应元素为wild
                        symbolArr[row][colum] = wildSymbolArr[row][colum];
                    } else {
                        // 当前坐标对应元素为wild，重新生成元素（非Scatter元素）
                        SymbolDo symbolDo = randomNonScatterSymbol(randomNumberList.get(randomNumberIndex), gameInfo, columProbabilisticMap);
                        symbolArr[row][colum] = symbolDo;
                        if (symbolDo.isWildSymbol()) {
                            wildSymbolArr[row][colum] = symbolDo;
                        }
                    }
                    randomNumberIndex++;
                }
            }
            WheelDo wheelDo = WheelDo.builder()
                    .index(number)
                    .symbolArr(symbolArr)
                    .build();
            wheels.add(wheelDo);
        }

        return wheels;
    }

    public Map<String, List<BigDecimal>> getProbabilisticConfig(GamePlayDo gamePlay) {
        if (gamePlay.isFreeSpinsFlag()) {
            return mariaMakilingConfig.getProbabilisticConfig().getFreePlayWheelProbabilisticConfig();
        } else {
            switch (gamePlay.getBetSubType()) {
                case NORMAL_BET:
                case BUY_BET:
                    return mariaMakilingConfig.getProbabilisticConfig().getCommonPlayWheelProbabilisticConfig();
                case FREE_SPINS_NORMAL_BET:
                case FREE_SPINS_BUY_BET:
                    return mariaMakilingConfig.getProbabilisticConfig().getFreePlayWheelProbabilisticConfig();
                default:
                    throw new BusinessException(ErrorCode.ERR_PARAM, "NOT SUPPORTED");
            }
        }
    }

    private Integer getWheelNumber() {
        int randomNumber = randomService.randomInt(1, 100);
        int startVal = 0;
        for (Map.Entry<Integer, Integer> entry : CollectionUtils.emptyIfNull(mariaMakilingConfig.getProbabilisticConfig().getFreeWheelTimesProbabilisticConfig().entrySet())) {
            if (randomNumber > startVal && randomNumber <= startVal + entry.getValue()) {
                return entry.getKey();
            } else {
                startVal += entry.getValue();
            }
        }
        throw new BusinessException(ErrorCode.ERR_INTER);
    }
}
