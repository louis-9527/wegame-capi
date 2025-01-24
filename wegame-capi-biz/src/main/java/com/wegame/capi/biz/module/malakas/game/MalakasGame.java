package com.wegame.capi.biz.module.malakas.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.common.enums.SymbolSizeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_GAME_INFO;
import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_SYMBOL_PLACEHOLDER;
import static com.wegame.capi.common.Constants.CONST_1000000;
import static com.wegame.capi.common.constants.GameConstants.*;

@Slf4j
@Component
public class MalakasGame extends AbstractGame<MalakasGameRecordDo> {

    private static final int MAX_REWARD_MULTIPLE = 2500;
    private static final int MAX_SCATTER_NUMBER_IN_REEL = 2;
    private static final int MAX_SCATTER_NUMBER_IN_WHEEL = 6;

    // ONE_COLUM_INDEX,THREE_COLUM_INDEX,FIVE_COLUM_INDEX顺序不可更改
    private static final List<Integer> SCATTER_COLUM_INDEX_LIST = Arrays.asList(
            ONE_COLUM_INDEX,
            THREE_COLUM_INDEX,
            FIVE_COLUM_INDEX);
    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    @Autowired
    MalakasConfig malakasConfig;
    @Autowired
    RewardHandler malakasRewardHandler;

    @Override
    public GameInfoDo getGameInfo() {
        return MALAKAS_GAME_INFO;
    }

    @Override
    public List<MalakasGameRecordDo> play(GamePlayDo gamePlay) {
        // todo 购买投注
        // 正常投注
        return normalPlay(gamePlay);
    }

    public List<MalakasGameRecordDo> normalPlay(GamePlayDo gamePlay) {
        List<MalakasGameRecordDo> ret = new ArrayList<>();
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        Long totalRewardAmount = 0L;
        // 生成-游戏滚轮
        WheelDo<MalakasSymbolDo> wheel = generateNormalWheel(gameInfo, getWheelProbabilisticConfig(gamePlay));
        // 组装&返回-游戏记录列表
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheel));
        MalakasGameRecordDo record = buildGameRecord(wheel, rewardResult);
        if (Objects.nonNull(record)) {
            ret.add(record);
            totalRewardAmount += rewardResult.getTotalRewardAmount();
        }
        while (canContinuePlay(rewardResult, totalRewardAmount)) {
            wheel = generateNormalWheelFromPreWheel(gameInfo, wheel, getWheelProbabilisticConfig(gamePlay), rewardResult);
            rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheel));
            MalakasGameRecordDo malakasGameRecord = buildGameRecord(wheel, rewardResult);
            if (Objects.nonNull(malakasGameRecord)) {
                ret.add(malakasGameRecord);
                totalRewardAmount += rewardResult.getTotalRewardAmount();
            }
        }
        // 返回-游戏记录列表
        return ret;
    }

    private WheelDo<MalakasSymbolDo> generateNormalWheelFromPreWheel(GameInfoDo gameInfo,
                                                                     WheelDo<MalakasSymbolDo> preWheel,
                                                                     Map<String, List<BigDecimal>> wheelProbabilisticConfig,
                                                                     RewardResultDo<MalakasSymbolDo> rewardResult) {
        // 复制：元素数组
        MalakasSymbolDo[][] symbolArr = copySymbolArr(preWheel.getSymbolArr());
        // 消除：中奖元素
        symbolArr = removeRewardSymbol(symbolArr, rewardResult);
        // todo
        // 重建：元素数组（非空元素按列下落处理）
        symbolArr = rebuildSymbolArr(symbolArr, gameInfo);

        // 生产：缺失元素


        return null;
    }

    /**
     * 重建元素数组：（非空元素按列下落处理）
     * @param symbolArr
     * @return
     */
    private MalakasSymbolDo[][] rebuildSymbolArr(MalakasSymbolDo[][] symbolArr, GameInfoDo gameInfoDo) {
        // 处理第一行：按行处理(非空元素左移，第一列元素&最后一列元素为空)
        int currentColum = 1;
        for(int colum = 1; colum < gameInfoDo.getColumn() - 1; colum ++) {
            if (Objects.nonNull(symbolArr[ONE_ROW_INDEX][colum])) {
                MalakasSymbolDo temp = symbolArr[ONE_ROW_INDEX][colum];
                symbolArr[ONE_ROW_INDEX][currentColum] = temp;
                if (!Objects.equals(currentColum, colum)) {
                    symbolArr[ONE_ROW_INDEX][colum] = null;
                }
            }
        }
        // 处理非第一行：按列处理（非空元素下移）
        for (int colum = ONE_COLUM_INDEX; colum < gameInfoDo.getColumn(); colum ++) {
            int currentRow = gameInfoDo.getRow() - 1;
            for (int row = gameInfoDo.getRow() - 1; row > 0; row--) {
                if (Objects.nonNull(symbolArr[row][colum])) {
                    MalakasSymbolDo temp = symbolArr[row][colum];
                    symbolArr[currentRow][colum] = temp;
                    if (!Objects.equals(currentRow, row)) {
                        symbolArr[currentRow][colum] = null;
                    }
                }
            }
        }
        return symbolArr;
    }

    private MalakasSymbolDo[][] removeRewardSymbol(MalakasSymbolDo[][] symbolArr, RewardResultDo<MalakasSymbolDo> rewardResult) {
        for (RewardLineDetailDo<MalakasSymbolDo> rewardLineDetail : CollectionUtils.emptyIfNull(rewardResult.getRewardLineDetailList())) {
            for (RewardSymbolDo<MalakasSymbolDo> rewardSymbol : CollectionUtils.emptyIfNull(rewardLineDetail.getRewardSymbolList())) {
                IndexDo indexDo = rewardSymbol.getIndex();
                if (Objects.equals(indexDo.getRowIndex(), ONE_ROW_INDEX)) {
                    // 第一行元素：直接消除
                    symbolArr[indexDo.getRowIndex()][indexDo.getColumIndex()] = null;
                } else {
                    // 非第一行元素：按照元素大小进行消除
                    int columSpanSiz = rewardSymbol.getSymbol().getSymbolSize().getColumSpan();
                    for (int columSpan = 0; columSpan < columSpanSiz; columSpan++) {
                        symbolArr[indexDo.getRowIndex() + columSpan][indexDo.getColumIndex()] = null;
                    }
                }

            }
        }
        return symbolArr;
    }

    public MalakasSymbolDo[][] copySymbolArr(MalakasSymbolDo[][] symbolArr) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        MalakasSymbolDo[][] newSymbolArr = new MalakasSymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                newSymbolArr[i][j] = symbolArr[i][j].copy();
            }
        }
        return newSymbolArr;
    }

    /**
     * 正常滚轮生成：生成逻辑无任何限制
     *
     * @param gameInfo 游戏信息
     * @return 滚轮
     */
    private WheelDo<MalakasSymbolDo> generateNormalWheel(GameInfoDo
                                                                 gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        MalakasSymbolDo[][] symbolArr = new MalakasSymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
        //        // 占位元素
        //        MalakasSymbolDo MALAKAS_SYMBOL_PLACEHOLDER = MalakasSymbolDo.builder()
        //                .id(Symbol1000.getId())
        //                .name(Symbol1000.getName())
        //                .symbolType(SymbolTypeEnum.COMMON)
        //                .symbolRule(null)
        //                .build();
        // 第一行第一列： 不生成元素,使用占位元素
        symbolArr[ONE_ROW_INDEX][ONE_COLUM_INDEX] = MALAKAS_SYMBOL_PLACEHOLDER;
        // 第一行最后一列： 不生成元素,使用占位元素
        symbolArr[ONE_ROW_INDEX][gameInfo.getColumn() - 1] = MALAKAS_SYMBOL_PLACEHOLDER;
        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, gameInfo.getRow() * gameInfo.getColumn());
        int randomNumberindex = 0;
        int scatterNumberInWheel = 0;
        for (int colum = ONE_COLUM_INDEX; colum < gameInfo.getColumn(); colum++) {
            // 一列只能包含一个scatter
            int scatterNumberInReel = 0;
            Map<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(colum, probabilisticConfig);
            for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
                if (Objects.nonNull(symbolArr[row][colum]) && Objects.equals(symbolArr[row][colum], MALAKAS_SYMBOL_PLACEHOLDER)) {
                    // 当前元素为占位元素， 则跳过
                    continue;
                }
                Integer randomNumber = randomNumberList.get(randomNumberindex);
                if (scatterNumberInReel >= MAX_SCATTER_NUMBER_IN_REEL || scatterNumberInWheel >= MAX_SCATTER_NUMBER_IN_WHEEL) {
                    // 生成非scatter
                    SymbolDo symbolDo = randomNonScatterSymbol(randomNumber, gameInfo, columProbabilisticMap);
                    SymbolSizeEnum symbolSize = generateSymbolSize(colum, row, gameInfo);
                    placeHolderSymbolHandle(symbolArr, row, colum, symbolSize);
                    // todo 生产元素金边
                    MalakasSymbolDo malakasSymbolDo = MalakasSymbolDo.buildFromSymbolDo(symbolDo, symbolSize);
                    symbolArr[row][colum] = malakasSymbolDo;
                } else {
                    // 随机生成
                    SymbolDo symbolDo = randomNormalSymbol(randomNumber, gameInfo, columProbabilisticMap);
                    SymbolSizeEnum symbolSize = generateSymbolSize(colum, row, gameInfo);
                    placeHolderSymbolHandle(symbolArr, row, colum, symbolSize);
                    // todo 生产元素金边
                    MalakasSymbolDo malakasSymbolDo = MalakasSymbolDo.buildFromSymbolDo(symbolDo, symbolSize);
                    symbolArr[row][colum] = malakasSymbolDo;
                    if (symbolDo.isScatterSymbol()) {
                        scatterNumberInReel++;
                        scatterNumberInWheel++;
                    }
                }
                randomNumberindex++;
            }
        }
        //        // 清空：占位元素
        //        MALAKAS_SYMBOL_PLACEHOLDER = null;
        // 返回：滚轮
        return WheelDo.<MalakasSymbolDo>builder()
                .index(WHEEL_DEFAULT_INDEX)
                .symbolArr(symbolArr)
                .build();
    }

    private boolean canContinuePlay(RewardResultDo<MalakasSymbolDo> rewardResult, Long totalRewardAmount) {
        if (Objects.isNull(rewardResult)) {
            return false;
        }
        if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
            return false;
        }
        if (totalRewardAmount / rewardResult.getBetAmount() >= MAX_REWARD_MULTIPLE) {
            log.warn("reach max reward multiple of {} bet. totalRewardAmount: {}, betAmount: {}", MAX_REWARD_MULTIPLE, totalRewardAmount, rewardResult.getBetAmount());
            return false;
        }
        return true;
    }

    private SymbolSizeEnum generateSymbolSize(int currentRow, int currentColum, GameInfoDo gameInfo) {
        if (Objects.equals(currentRow, ONE_ROW_INDEX)
                || Objects.equals(currentColum, ONE_COLUM_INDEX)
                || Objects.equals(currentColum, gameInfo.getColumn() - 1)) {
            // 第一行，第一列，最后一列：只能为小元素
            return SymbolSizeEnum.S;
        }
        Map<String, List<BigDecimal>> symbolSizeProbabilisticConfig = malakasConfig.getProbabilisticConfig().getSymbolSizeProbabilisticConfig();
        Map<String, Integer> map = buildColumSymboleSizeProbabilisticMap(currentColum, gameInfo.getRow() - currentRow, symbolSizeProbabilisticConfig);
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        return randomSymbolSize(map, randomService.randomInt(1, map.values()
                .stream()
                .reduce(0, Integer::sum)));
    }


    public Map<String, Integer> buildColumSymboleSizeProbabilisticMap(int currentColumNo, int maxColumSpan, Map<
            String, List<BigDecimal>> sizeProbabilisticConfigMap) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(sizeProbabilisticConfigMap), ErrorCode.ERR_INTER);
        Map<String, Integer> map = new HashMap<>();
        sizeProbabilisticConfigMap.entrySet().forEach(item -> {
            SymbolSizeEnum symbolSize = SymbolSizeEnum.valueOfCode(item.getKey());
            int val = (item.getValue().get(currentColumNo).multiply(new BigDecimal(Constants.CONST_10000))).intValue();
            if (val > 0 && maxColumSpan >= symbolSize.getColumSpan()) {
                map.put(item.getKey(), val);
            }
        });
        return map;
    }

    private void placeHolderSymbolHandle(MalakasSymbolDo[][] symbolArr, int row, int colum, SymbolSizeEnum symbolSize) {
        switch (symbolSize) {
            case S:
                break;
            case M:
                symbolArr[row + 1][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                break;
            case L:
                symbolArr[row + 1][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                symbolArr[row + 2][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                break;
            case XL:
                symbolArr[row + 1][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                symbolArr[row + 2][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                symbolArr[row + 3][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                break;
        }
    }

    private Map<String, List<BigDecimal>> getWheelProbabilisticConfig(GamePlayDo gamePlay) {
        if (gamePlay.isFreeSpinsFlag()) {
            return malakasConfig.getProbabilisticConfig().getFreePlayWheelProbabilisticConfig();
        } else {
            switch (gamePlay.getBetSubType()) {
                case NORMAL_BET:
                case BUY_BET:
                    return malakasConfig.getProbabilisticConfig().getCommonPlayWheelProbabilisticConfig();
                case FREE_SPINS_NORMAL_BET:
                case FREE_SPINS_BUY_BET:
                    return malakasConfig.getProbabilisticConfig().getFreePlayWheelProbabilisticConfig();
                default:
                    throw new BusinessException(ErrorCode.ERR_PARAM, "NOT SUPPORTED");
            }
        }
    }

    private MalakasGameRecordDo buildGameRecord
            (WheelDo<MalakasSymbolDo> wheel, RewardResultDo<MalakasSymbolDo> rewardResult) {
        return MalakasGameRecordDo.builder()
                .index(wheel.getIndex())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .build();
    }
    ///// 分割线 /////


//    private List<WheelDo<MalakasSymbolDo>> generate(GamePlayDo gamePlay) {
//        GameInfoDo gameInfo = getGameInfo();
//        Map<String, List<BigDecimal>> probabilisticConfig = getProbabilisticConfig(gamePlay);
//        if (gamePlay.isFreeSpinsFlag()) {
//            return generateMultiWheel(gameInfo, probabilisticConfig);
//        } else {
//            // 根据投注类型：生成游戏滚轮
//            switch (gamePlay.getBetType()) {
//                case NORMAL_BET:
//                    return Collections.singletonList(generateNormalWheel(gameInfo, probabilisticConfig));
//                case BUY_BET:
//                    return Collections.singletonList(generateFreeSpinsWheel(gameInfo, probabilisticConfig));
//                default:
//                    throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
//            }
//        }
//    }
//

//
//    /**
//     * 免费滚轮生成：中奖Scatter
//     *
//     * @param gameInfo 游戏信息
//     * @return 滚轮
//     */
//    private WheelDo<MalakasSymbolDo> generateFreeSpinsWheel(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
//        WheelDo<MalakasSymbolDo> wheelDo = generateNormalWheel(gameInfo, probabilisticConfig);
//        MalakasSymbolDo[][] symbolArr = wheelDo.getSymbolArr();
//        // 生成整盘随机数(SCATTER_COLUM_INDEX_LIST.size())个
//        List<Integer> randomNumberList = randomService.randomIntList(0, gameInfo.getRow() - 1, SCATTER_COLUM_INDEX_LIST.size());
//        int randomNumberIndex = 0;
//        // 检查1，3，5列是否有Scatter，如果不存在，则1，3，5列各自随机一行的的元素设置为scatter
//        for (Integer colum : SCATTER_COLUM_INDEX_LIST) {
//            boolean hasScatter = false;
//            for (int i = 0; i < gameInfo.getRow(); i++) {
//                MalakasSymbolDo symbol = symbolArr[i][colum];
//                if (symbol.isScatterSymbol()) {
//                    hasScatter = true;
//                    break;
//                }
//            }
//            if (!hasScatter) {
//                // 生成一个0到行之间的随机整数
//                int randomRow = randomNumberList.get(randomNumberIndex++);
//                symbolArr[randomRow][colum] = MARIA_MAKILING_SCATTER;
//            }
//        }
//        return WheelDo.<MalakasSymbolDo>builder()
//                .index(WHEEL_DEFAULT_INDEX)
//                .symbolArr(symbolArr)
//                .build();
//    }
//
//    /**
//     * 生成多个滚轮
//     * 1.无 Scatter
//     * 2.wild需要保留
//     *
//     * @param gameInfo 游戏信息
//     * @return 滚轮列表
//     */
//    private List<WheelDo<MalakasSymbolDo>> generateMultiWheel(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
//        // 保存全局wild
//        MalakasSymbolDo[][] wildSymbolArr = new MalakasSymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
//        // 免费次数
//        int wheelNumber = getWheelNumber();
//        // 生成轮盘总和
//        List<WheelDo<MalakasSymbolDo>> wheels = new ArrayList<>();
//        // 生成整盘随机数(row*column)
//        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, wheelNumber * gameInfo.getRow() * gameInfo.getColumn());
//        int randomNumberIndex = 0;
//        for (int number = 1; number <= wheelNumber; number++) {
//            MalakasSymbolDo[][] symbolArr = new MalakasSymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
//            for (int colum = 0; colum < gameInfo.getColumn(); colum++) {
//                // 当前列元素概率
//                Map<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(colum, probabilisticConfig);
//                for (int row = 0; row < gameInfo.getRow(); row++) {
//                    if (wildSymbolArr[row][colum] != null) {
//                        // 当前坐标对应元素为wild
//                        symbolArr[row][colum] = wildSymbolArr[row][colum];
//                    } else {
//                        // 当前坐标对应元素为wild，重新生成元素（非Scatter元素）
//                        MalakasSymbolDo symbolDo = randomNonScatterSymbol(randomNumberList.get(randomNumberIndex), gameInfo, columProbabilisticMap);
//                        symbolArr[row][colum] = symbolDo;
//                        if (symbolDo.isWildSymbol()) {
//                            wildSymbolArr[row][colum] = symbolDo;
//                        }
//                    }
//                    randomNumberIndex++;
//                }
//            }
//            WheelDo<MalakasSymbolDo> wheelDo = WheelDo.<MalakasSymbolDo>builder()
//                    .index(number)
//                    .symbolArr(symbolArr)
//                    .build();
//            wheels.add(wheelDo);
//        }
//
//        return wheels;
//    }
//

//
//    private Integer getWheelNumber() {
//        int randomNumber = randomService.randomInt(1, 100);
//        int startVal = 0;
//        for (Map.Entry<Integer, Integer> entry : CollectionUtils.emptyIfNull(malakasConfig.getProbabilisticConfig().getFreeWheelTimesProbabilisticConfig().entrySet())) {
//            if (randomNumber > startVal && randomNumber <= startVal + entry.getValue()) {
//                return entry.getKey();
//            } else {
//                startVal += entry.getValue();
//            }
//        }
//        throw new BusinessException(ErrorCode.ERR_INTER);
//    }
}
