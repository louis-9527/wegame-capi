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
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.biz.module.malakas.MalakasProbabilisticUtil;
import com.wegame.capi.biz.module.malakas.MalakasSymbolUtil;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.common.enums.SymbolSizeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_GAME_INFO;
import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_SYMBOL_PLACEHOLDER;
import static com.wegame.capi.biz.module.malakas.MalakasProbabilisticUtil.getWheelProbabilisticConfig;
import static com.wegame.capi.biz.module.malakas.MalakasSymbolUtil.*;
import static com.wegame.capi.common.Constants.CONST_10000;
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
        switch (gamePlay.getBetType()) {
            case NORMAL_BET:
                // 正常投注
                return normalPlay(gamePlay);
            case BUY_BET:
                // 购买投注
                return buyPlay(gamePlay);
            default:
                throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
        }
    }

    public List<MalakasGameRecordDo> normalPlay(GamePlayDo gamePlay) {
        List<MalakasGameRecordDo> ret = new ArrayList<>();
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        Long totalRewardAmount = 0L;
        // 生成-游戏滚轮
        WheelDo<MalakasSymbolDo> wheel = generateNormalWheel(gameInfo, getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig()));
        // 组装&返回-游戏记录列表
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheel));
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        MalakasGameRecordDo record = buildGameRecord(wheel, rewardResult);
        if (Objects.nonNull(record)) {
            ret.add(record);
            totalRewardAmount += rewardResult.getTotalRewardAmount();
        }
        while (canContinuePlay(rewardResult, totalRewardAmount)) {
            if (rewardResult.isHasRewardFreeSpin()) {
                wheel = generateNormalWheelFromPreWheel(gameInfo, wheel, getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig()), rewardResult);
            } else {
                wheel = generateNormalWheelFromPreWheel(gameInfo, wheel, getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig()), rewardResult);
            }
            rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheel));
            MalakasGameRecordDo malakasGameRecord = buildGameRecord(wheel, rewardResult);
            if (Objects.nonNull(malakasGameRecord)) {
                ret.add(malakasGameRecord);
                totalRewardAmount += rewardResult.getTotalRewardAmount();
            }
        }
        // 统一处理占位元素：设置为空
        postPlaceholderSymbol(ret);
        // 返回-游戏记录列表
        return ret;
    }

    public List<MalakasGameRecordDo> buyPlay(GamePlayDo gamePlay) {
        List<MalakasGameRecordDo> ret = new ArrayList<>();
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        Long totalRewardAmount = 0L;
        // 生成-游戏滚轮
        WheelDo<MalakasSymbolDo> wheel = generateNormalWheel(gameInfo, getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig()));
        // 组装&返回-游戏记录列表
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheel));
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        MalakasGameRecordDo record = buildGameRecord(wheel, rewardResult);
        if (Objects.nonNull(record)) {
            ret.add(record);
            totalRewardAmount += rewardResult.getTotalRewardAmount();
        }
        while (canContinuePlay(rewardResult, totalRewardAmount)) {
            if (rewardResult.isHasRewardFreeSpin()) {
                // todo fix 生产免费轮
                wheel = generateNormalWheelFromPreWheel(gameInfo, wheel, getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig()), rewardResult);
            } else {
                wheel = generateNormalWheelFromPreWheel(gameInfo, wheel, getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig()), rewardResult);
            }
            rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheel));
            MalakasGameRecordDo malakasGameRecord = buildGameRecord(wheel, rewardResult);
            if (Objects.nonNull(malakasGameRecord)) {
                ret.add(malakasGameRecord);
                totalRewardAmount += rewardResult.getTotalRewardAmount();
            }
        }
        // 统一处理占位元素：设置为空
        postPlaceholderSymbol(ret);
        // 返回-游戏记录列表
        return ret;
    }


    private WheelDo<MalakasSymbolDo> generateNormalWheelFromPreWheel(GameInfoDo gameInfo,
                                                                     WheelDo<MalakasSymbolDo> preWheel,
                                                                     Map<String, List<BigDecimal>> probabilisticConfig,
                                                                     RewardResultDo<MalakasSymbolDo> rewardResult) {
        // 复制：元素数组
        MalakasSymbolDo[][] symbolArr = copySymbolArr(preWheel.getSymbolArr());
        // 消除：中奖元素
        clearRewardSymbol(symbolArr, rewardResult);
        // 重建：元素数组（非空元素按列下落处理：第1-6Reel非空元素向下移动，第7Reel非空元素向左移动）
        rebuildSymbolArr(symbolArr, gameInfo);
        // 生产：缺失元素(填补剩余元素)
        return generateCommonNormalWheel(gameInfo, symbolArr, probabilisticConfig, preWheel.getIndex() + 1, WHEEL_DEFAULT_INDEX);
    }

    /**
     * 正常滚轮生成：生成逻辑无任何限制
     *
     * @param gameInfo 游戏信息
     * @return 滚轮
     */
    private WheelDo<MalakasSymbolDo> generateNormalWheel(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        MalakasSymbolDo[][] symbolArr = new MalakasSymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
        // 第一行第一列： 不生成元素,使用占位元素
        symbolArr[ONE_ROW_INDEX][ONE_COLUM_INDEX] = MALAKAS_SYMBOL_PLACEHOLDER;
        // 第一行最后一列： 不生成元素,使用占位元素
        symbolArr[ONE_ROW_INDEX][gameInfo.getColumn() - 1] = MALAKAS_SYMBOL_PLACEHOLDER;
        return generateCommonNormalWheel(gameInfo, symbolArr, probabilisticConfig, WHEEL_DEFAULT_INDEX, WHEEL_DEFAULT_INDEX);
    }

    private WheelDo<MalakasSymbolDo> generateCommonNormalWheel(GameInfoDo gameInfo,
                                                               MalakasSymbolDo[][] symbolArr,
                                                               Map<String, List<BigDecimal>> probabilisticConfig,
                                                               int wheelIndex,
                                                               int parentIndex) {
        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, gameInfo.getRow() * gameInfo.getColumn());
        int randomNumberIndex = 0;
        int scatterNumberInWheel = 0;
        for (int colum = ONE_COLUM_INDEX; colum < gameInfo.getColumn(); colum++) {
            // 一列只能包含两个scatter
            int scatterNumberInReel = 0;
            Map<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(colum, probabilisticConfig);
            for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
                if (Objects.nonNull(symbolArr[row][colum])) {
                    // 当前元素非空，则跳过
                    continue;
                }
                Integer randomNumber = randomNumberList.get(randomNumberIndex);
                if (scatterNumberInReel >= MAX_SCATTER_NUMBER_IN_REEL || scatterNumberInWheel >= MAX_SCATTER_NUMBER_IN_WHEEL || Objects.equals(row, ONE_ROW_INDEX)) {
                    // 第一行 || 列scatter达到上限 || 滚轮scatter达到上限，则生产非scatter元素
                    // 生成非scatter
                    SymbolDo symbolDo = randomNonScatterSymbol(randomNumber, gameInfo, columProbabilisticMap);
                    SymbolSizeEnum symbolSize = generateSymbolSize(row, MalakasSymbolUtil.getMaxRow(symbolArr, row, colum), colum, gameInfo);
                    MalakasSymbolUtil.handlePlaceholderSymbol(symbolArr, row, colum, symbolSize);
                    // 构建元素金边
                    boolean symbolGoldBorder = generateSymbolGoldBorder(gameInfo, row, colum);
                    MalakasSymbolDo malakasSymbolDo = MalakasSymbolDo.buildFromSymbol(symbolDo, symbolSize, symbolGoldBorder);
                    symbolArr[row][colum] = malakasSymbolDo;
                } else {
                    // 随机生成
                    SymbolDo symbolDo = randomNormalSymbol(randomNumber, gameInfo, columProbabilisticMap);
                    SymbolSizeEnum symbolSize = generateSymbolSize(row, MalakasSymbolUtil.getMaxRow(symbolArr, row, colum), colum, gameInfo);
                    MalakasSymbolUtil.handlePlaceholderSymbol(symbolArr, row, colum, symbolSize);
                    // 构建元素金边
                    boolean symbolGoldBorder = generateSymbolGoldBorder(gameInfo, row, colum);
                    MalakasSymbolDo malakasSymbolDo = MalakasSymbolDo.buildFromSymbol(symbolDo, symbolSize, symbolGoldBorder);
                    symbolArr[row][colum] = malakasSymbolDo;
                    if (symbolDo.isScatterSymbol()) {
                        scatterNumberInReel++;
                        scatterNumberInWheel++;
                    }
                }
                randomNumberIndex++;
            }
        }
        // 返回：滚轮
        return WheelDo.<MalakasSymbolDo>builder()
                .index(wheelIndex)
                .parentIndex(parentIndex)
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

    private SymbolSizeEnum generateSymbolSize(int currentRow, int maxRow, int currentColum, GameInfoDo gameInfo) {
        if (Objects.equals(currentRow, ONE_ROW_INDEX)
                || Objects.equals(currentColum, ONE_COLUM_INDEX)
                || Objects.equals(currentColum, gameInfo.getColumn() - 1)) {
            // 第一行，第一列，最后一列：只能为小元素
            return SymbolSizeEnum.S;
        }
        Map<String, Integer> map = MalakasProbabilisticUtil.buildColumSymboleSizeProbabilisticMap(currentColum, maxRow - currentRow + 1, malakasConfig.getProbabilisticConfig());
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        return randomSymbolSize(map, randomService.randomInt(1, map.values()
                .stream()
                .reduce(0, Integer::sum)));
    }

    private boolean generateSymbolGoldBorder(GameInfoDo gameInfo, int row, int colum) {
        List<BigDecimal> goldBoarderProbabilisticConfig = malakasConfig.getProbabilisticConfig().getGoldBoarderProbabilisticConfig();
        BigDecimal goldBoarderProbabilistic = null;
        if (Objects.equals(row, ONE_ROW_INDEX)) {
            goldBoarderProbabilistic = goldBoarderProbabilisticConfig.get(gameInfo.getColumn() - 1);
        } else {
            goldBoarderProbabilistic = goldBoarderProbabilisticConfig.get(colum);
        }
        AssertUtil.assertNotNull(goldBoarderProbabilistic, ErrorCode.ERR_INTER);
        int probabilisticVal = goldBoarderProbabilistic.multiply(new BigDecimal(100)).intValue();
        if (Objects.equals(probabilisticVal, 0)) {
            return false;
        } else {
            int randomVal = randomService.randomInt(1, CONST_10000);
            return randomVal > 0 && randomVal <= probabilisticVal;
        }
    }

    private MalakasGameRecordDo buildGameRecord(WheelDo<MalakasSymbolDo> wheel, RewardResultDo<MalakasSymbolDo> rewardResult) {
        return MalakasGameRecordDo.builder()
                .index(wheel.getIndex())
                .parentIndex(wheel.getParentIndex())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .build();
    }
}
