package com.wegame.capi.biz.module.mariamakiling.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractDynamicOperationGame;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static com.wegame.capi.biz.module.base.AmountUtil.maxAmountControl;
import static com.wegame.capi.biz.module.base.AmountUtil.reachMaxMultiple;
import static com.wegame.capi.biz.module.mariamakiling.MariaMakilingGameHelper.MARIA_MAKILING_GAME_INFO;
import static com.wegame.capi.biz.module.mariamakiling.MariaMakilingGameHelper.MARIA_MAKILING_SCATTER;
import static com.wegame.capi.common.Constants.CONST_1000000;
import static com.wegame.capi.common.constants.GameConstants.*;

@Slf4j
@Component
public class MariaMakilingGame extends AbstractDynamicOperationGame<MariaMakilingGameRecordDo> {

    // ONE_COLUM_INDEX,THREE_COLUM_INDEX,FIVE_COLUM_INDEX顺序不可更改
    private static final List<Integer> SCATTER_COLUM_INDEX_LIST = Arrays.asList(
            ONE_COLUM_INDEX,
            THREE_COLUM_INDEX,
            FIVE_COLUM_INDEX);
    private static final Integer DEFAULT_WHEEL_INDEX = 0;
    private static final Long GAME_RECORD_DEFAULT_PARENT_ID = 0L;

    @Autowired
    MariaMakilingConfig mariaMakilingConfig;
    @Autowired
    RewardHandler mariaMakilingRewardHandler;

    @Override
    public GameInfoDo getGameInfo() {
        return MARIA_MAKILING_GAME_INFO;
    }

    @Override
    public List<MariaMakilingGameRecordDo> normalPlay(GamePlayDo gamePlay) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        List<MariaMakilingGameRecordDo> ret = new ArrayList<>();
        WheelDo<SymbolDo> firstWheel;
        long firstGameRecordId = generateGameRecordId();
        List<MariaMakilingGameRecordDo> freeWheelGameRecordList = null;
        List<Long> freeWheelGameRecordIds = null;
        // 根据投注类型：生成游戏滚轮
        switch (gamePlay.getBetType()) {
            case NORMAL_BET:
                firstWheel = generateNormalWheel(gameInfo, getProbabilisticConfig(gamePlay, false));
                break;
            case BUY_BET:
                firstWheel = generateRewardFreeWheel(gameInfo, getProbabilisticConfig(gamePlay, false));
                break;
            default:
                throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
        }
        AssertUtil.assertNotNull(firstWheel, ErrorCode.ERR_INTER);
        RewardResultDo<SymbolDo> firstWheelRewardResult = mariaMakilingRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, firstWheel, BigDecimal.ZERO));
        boolean firstWheelReachMaxRewardMultiple = reachMaxMultiple(firstWheelRewardResult.getTotalRewardAmount(), gamePlay.getPayAmount(), mariaMakilingConfig.getRewardMaxMultiple());
        if (!firstWheelReachMaxRewardMultiple && firstWheelRewardResult.isHasRewardFreeWheel()) {
            // 首轮未达到最大倍数 && 首轮命中免费轮
            freeWheelGameRecordList = new ArrayList<>();
            freeWheelGameRecordIds = new ArrayList<>();
            List<WheelDo<SymbolDo>> wheelList = generateMultiWheel(gameInfo, getProbabilisticConfig(gamePlay, true));// 累计中奖金额
            long overallRewardAmount = firstWheelRewardResult.getTotalRewardAmount();
            for (WheelDo<SymbolDo> wheelDo : CollectionUtils.emptyIfNull(wheelList)) {
                RewardResultDo rewardResultDo = mariaMakilingRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheelDo, BigDecimal.ZERO));
                overallRewardAmount = overallRewardAmount + rewardResultDo.getTotalRewardAmount();
                boolean freeWheelReachMaxRewardMultiple = reachMaxMultiple(overallRewardAmount, gamePlay.getPayAmount(), mariaMakilingConfig.getRewardMaxMultiple());
                // 构建免费轮游戏记录
                MariaMakilingGameRecordDo gameRecord = MariaMakilingGameRecordDo.builder()
                        .id(generateGameRecordId())
                        .parentId(GAME_RECORD_DEFAULT_PARENT_ID)
                        .betRecordId(gamePlay.getBetRecordId())
                        .batchId(firstGameRecordId)
                        .playerId(userInfo.getPlayerId())
                        .playerName(userInfo.getPlayerName())
                        .index(wheelDo.getIndex())
                        .wheel(wheelDo)
                        .rewardResult(rewardResultDo)
                        .gameRecordIds(null)
                        .overallRewardAmount(maxAmountControl(overallRewardAmount, gamePlay.getPayAmount(), mariaMakilingConfig.getRewardMaxMultiple()))
                        .accumulateReachMaxMultiple(freeWheelReachMaxRewardMultiple)
                        .lastGameFlag(freeWheelReachMaxRewardMultiple)
                        .build();
                freeWheelGameRecordList.add(gameRecord);
                freeWheelGameRecordIds.add(gameRecord.getId());
                if (freeWheelReachMaxRewardMultiple) {
                    break;
                }
            }
        }
        // 构建首轮游戏记录

        MariaMakilingGameRecordDo firstGameRecord = MariaMakilingGameRecordDo.builder()
                .id(firstGameRecordId)
                .parentId(GAME_RECORD_DEFAULT_PARENT_ID)
                .betRecordId(gamePlay.getBetRecordId())
                .batchId(0L)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(firstWheel.getIndex())
                .wheel(firstWheel)
                .rewardResult(firstWheelRewardResult)
                .gameRecordIds(freeWheelGameRecordIds)
                .overallRewardAmount(maxAmountControl(firstWheelRewardResult.getTotalRewardAmount(), gamePlay.getPayAmount(), mariaMakilingConfig.getRewardMaxMultiple()))
                .accumulateReachMaxMultiple(firstWheelReachMaxRewardMultiple)
                .lastGameFlag(!firstWheelReachMaxRewardMultiple && firstWheelRewardResult.isHasRewardFreeWheel())
                .build();
        ret.add(firstGameRecord);
        if (CollectionUtils.isNotEmpty(freeWheelGameRecordList)) {
            ret.addAll(freeWheelGameRecordList);
        }
        return ret;
    }

    @Override
    public List<MariaMakilingGameRecordDo> eventPlay(GamePlayDo gamePlay, List<WheelDo<SymbolDo>> wheelDos) {
        GameInfoDo gameInfo = getGameInfo();
        UserInfo userInfo = UserInfoContext.getUserInfo();
        WheelDo<SymbolDo> firstWheel = wheelDos.get(0);
        // 生成首轮结果
        long firstGameRecordId = generateGameRecordId();
        RewardResultDo<SymbolDo> firstWheelRewardResult = mariaMakilingRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheelDos.get(0), BigDecimal.ZERO));
        long overallRewardAmount = firstWheelRewardResult.getTotalRewardAmount();
        List<MariaMakilingGameRecordDo> freeWheelGameRecordList = new ArrayList<>();
        List<Long> freeWheelGameRecordIds = new ArrayList<>();
        if (wheelDos.size() > 1) {
            // 命中了免费轮
            for (int i = 1; i < wheelDos.size(); i++) {
                WheelDo<SymbolDo> wheelDo = wheelDos.get(i);
                RewardResultDo<SymbolDo> rewardResultDo = mariaMakilingRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheelDo, BigDecimal.ZERO));
                overallRewardAmount = overallRewardAmount + rewardResultDo.getTotalRewardAmount();
                MariaMakilingGameRecordDo gameRecord = this.buildGameRecord(
                        userInfo,
                        generateGameRecordId(),
                        gamePlay.getBetRecordId(),
                        wheelDo,
                        rewardResultDo,
                        null,
                        overallRewardAmount,
                        false,
                        false);
                freeWheelGameRecordList.add(gameRecord);
                freeWheelGameRecordIds.add(gameRecord.getId());

            }
        }

        // 构建首轮
        MariaMakilingGameRecordDo firstGameRecord = this.buildGameRecord(userInfo,
                firstGameRecordId,
                gamePlay.getBetRecordId(),
                firstWheel,
                firstWheelRewardResult,
                freeWheelGameRecordIds,
                firstWheelRewardResult.getTotalRewardAmount(),
                false,
                firstWheelRewardResult.isHasRewardFreeWheel()
        );
        List<MariaMakilingGameRecordDo> ret = new ArrayList<>();
        ret.add(firstGameRecord);
        if(CollectionUtils.isNotEmpty(freeWheelGameRecordList)){
            ret.addAll(freeWheelGameRecordList);
        }

        return ret;
    }


    public MariaMakilingGameRecordDo buildGameRecord(UserInfo userInfo,
                                                     Long gameRecordId,
                                                     Long betRecordId,
                                                     WheelDo<SymbolDo> wheel,
                                                     RewardResultDo<SymbolDo> rewardResult,
                                                     List<Long> freeSpinGameRecordIds,
                                                     Long TotalRewardAmount,
                                                     Boolean accumulateReachMaxMultiple,
                                                     Boolean lastGameFlag) {
        return MariaMakilingGameRecordDo.builder()
                .id(gameRecordId)
                .parentId(GAME_RECORD_DEFAULT_PARENT_ID)
                .betRecordId(betRecordId)
                .batchId(0L)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(wheel.getIndex())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(freeSpinGameRecordIds)
                .overallRewardAmount(TotalRewardAmount)
                .accumulateReachMaxMultiple(accumulateReachMaxMultiple)
                .lastGameFlag(lastGameFlag)
                .build();
    }

    /**
     * 正常滚轮生成：生成逻辑无任何限制
     *
     * @param gameInfo 游戏信息
     * @return 滚轮
     */
    private WheelDo<SymbolDo> generateNormalWheel(GameInfoDo gameInfo, TreeMap<String, List<BigDecimal>> probabilisticConfig) {
        SymbolDo[][] symbolArr = new SymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, gameInfo.getRow() * gameInfo.getColumn());
        int randomNumberindex = 0;
        for (int column = 0; column < gameInfo.getColumn(); column++) {
            // 一列只能包含一个scatter
            boolean hasScatter = false;
            TreeMap<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(column, probabilisticConfig);
            for (int row = 0; row < gameInfo.getRow(); row++) {
                Integer random = randomNumberList.get(randomNumberindex);
                if (hasScatter) {
                    // 生成非scatter
                    SymbolDo symbolDo = randomNonScatterSymbol(random, gameInfo, columProbabilisticMap);
                    symbolArr[row][column] = symbolDo;
                } else {
                    // 随机生成
                    SymbolDo symbolDo = randomNormalSymbol(random, gameInfo, columProbabilisticMap);
                    symbolArr[row][column] = symbolDo;
                    hasScatter = symbolDo.isScatterSymbol();
                }
                randomNumberindex++;
            }
        }
        return WheelDo.<SymbolDo>builder()
                .index(DEFAULT_WHEEL_INDEX)
                .symbolArr(symbolArr).build();
    }

    /**
     * 免费滚轮生成：中奖Scatter
     *
     * @param gameInfo 游戏信息
     * @return 滚轮
     */
    private WheelDo<SymbolDo> generateRewardFreeWheel(GameInfoDo gameInfo, TreeMap<String, List<BigDecimal>> probabilisticConfig) {
        WheelDo<SymbolDo> wheelDo = generateNormalWheel(gameInfo, probabilisticConfig);
        SymbolDo[][] symbolArr = wheelDo.getSymbolArr();
        // 生成整盘随机数(SCATTER_COLUM_INDEX_LIST.size())个
        List<Integer> randomNumberList = randomService.randomIntList(0, gameInfo.getRow() - 1, SCATTER_COLUM_INDEX_LIST.size());
        int randomNumberIndex = 0;
        // 检查1，3，5列是否有Scatter，如果不存在，则1，3，5列各自随机一行的的元素设置为scatter
        for (Integer column : SCATTER_COLUM_INDEX_LIST) {
            boolean hasScatter = false;
            for (int i = 0; i < gameInfo.getRow(); i++) {
                SymbolDo symbol = symbolArr[i][column];
                if (symbol.isScatterSymbol()) {
                    hasScatter = true;
                    break;
                }
            }
            if (!hasScatter) {
                // 生成一个0到行之间的随机整数
                int randomRow = randomNumberList.get(randomNumberIndex++);
                symbolArr[randomRow][column] = MARIA_MAKILING_SCATTER;
            }
        }
        return WheelDo.<SymbolDo>builder()
                .index(DEFAULT_WHEEL_INDEX)
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
    private List<WheelDo<SymbolDo>> generateMultiWheel(GameInfoDo gameInfo, TreeMap<String, List<BigDecimal>> probabilisticConfig) {
        // 保存全局wild
        SymbolDo[][] wildSymbolArr = new SymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
        // 免费次数
        int wheelNumber = getFreeWheelNumber();
        if (wheelNumber == 0) {
            return null;
        }
        // 生成轮盘总和
        List<WheelDo<SymbolDo>> wheels = new ArrayList<>();
        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, wheelNumber * gameInfo.getRow() * gameInfo.getColumn());
        int randomNumberIndex = 0;
        for (Integer number = 1; number <= wheelNumber; number++) {
            SymbolDo[][] symbolArr = new SymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
            for (int column = 0; column < gameInfo.getColumn(); column++) {
                // 当前列元素概率
                TreeMap<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(column, probabilisticConfig);
                for (int row = 0; row < gameInfo.getRow(); row++) {
                    if (Objects.nonNull(wildSymbolArr[row][column])) {
                        // 当前坐标对应元素为wild
                        symbolArr[row][column] = wildSymbolArr[row][column];
                    } else {
                        // 当前坐标对应元素为wild，重新生成(非Scatter)元素
                        SymbolDo symbolDo = randomNonScatterSymbol(randomNumberList.get(randomNumberIndex), gameInfo, columProbabilisticMap);
                        symbolArr[row][column] = symbolDo;
                        if (symbolDo.isWildSymbol()) {
                            wildSymbolArr[row][column] = symbolDo;
                        }
                    }
                    randomNumberIndex++;
                }
            }
            wheels.add(WheelDo.<SymbolDo>builder()
                    .index(number)
                    .symbolArr(symbolArr)
                    .build());
        }
        return wheels;
    }

    public TreeMap<String, List<BigDecimal>> getProbabilisticConfig(GamePlayDo gamePlay, boolean isFreeSpinsFlag) {
        if (isFreeSpinsFlag) {
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

    private Integer getFreeWheelNumber() {
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
