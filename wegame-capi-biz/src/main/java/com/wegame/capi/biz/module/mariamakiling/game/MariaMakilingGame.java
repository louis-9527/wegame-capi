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
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.DynamicOperationEventKindEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

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
    public RewardHandler rewardHandler() {
        return mariaMakilingRewardHandler;
    }

    @Override
    public List<MariaMakilingGameRecordDo> normalPlay(GamePlayDo gamePlay) {
        List<MariaMakilingGameRecordDo> ret = new ArrayList<>();
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        WheelDo<SymbolDo> firstWheel;
        long firstGameRecordId = generateGameRecordId();
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
        RewardResultDo<SymbolDo> rewardResult = mariaMakilingRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, firstWheel, BigDecimal.ZERO));
        // 判断-是否命中免费轮
        List<Long> freeWheelGameRecordIds = null;
        if (rewardResult.isHasRewardFreeWheel()) {
            freeWheelGameRecordIds = getGameRecordIdList(getWheelNumber());
        }
        MariaMakilingGameRecordDo record = buildGameRecord(firstGameRecordId,
                GAME_RECORD_DEFAULT_PARENT_ID,
                gamePlay.getBetRecordId(),
                0L,
                firstWheel,
                rewardResult,
                freeWheelGameRecordIds);
        ret.add(record);
        // 判断是否命中免费轮
        if (CollectionUtils.isNotEmpty(freeWheelGameRecordIds)) {
            List<WheelDo<SymbolDo>> wheelList = generateMultiWheel(gameInfo, getProbabilisticConfig(gamePlay, true), freeWheelGameRecordIds);
            AssertUtil.assertNotNull(wheelList.size() == freeWheelGameRecordIds.size(), ErrorCode.ERR_INTER);
            // 组装&返回-游戏记录列表
            for (int index = 0; index < wheelList.size(); index++) {
                WheelDo<SymbolDo> wheelDo = wheelList.get(index);
                Long gameRecordId = freeWheelGameRecordIds.get(index);
                MariaMakilingGameRecordDo gameRecord = buildGameRecord(gameRecordId,
                        GAME_RECORD_DEFAULT_PARENT_ID,
                        record.getBetRecordId(),
                        record.getId(),
                        wheelDo,
                        mariaMakilingRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheelDo, BigDecimal.ZERO)),
                        null);
                ret.add(gameRecord);
            }
        }
        return ret;
    }

    /**
     * 正常滚轮生成：生成逻辑无任何限制
     *
     * @param gameInfo 游戏信息
     * @return 滚轮
     */
    private WheelDo<SymbolDo> generateNormalWheel(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
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
    private WheelDo<SymbolDo> generateRewardFreeWheel(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        WheelDo<SymbolDo> wheelDo = generateNormalWheel(gameInfo, probabilisticConfig);
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
    private List<WheelDo<SymbolDo>> generateMultiWheel(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig, List<Long> freeSpinGameRecordIds) {
        if (CollectionUtils.isEmpty(freeSpinGameRecordIds)) {
            return new ArrayList<>();
        }
        // 保存全局wild
        SymbolDo[][] wildSymbolArr = new SymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
        // 免费次数
        int wheelNumber = freeSpinGameRecordIds.size();
        // 生成轮盘总和
        List<WheelDo<SymbolDo>> wheels = new ArrayList<>();
        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, wheelNumber * gameInfo.getRow() * gameInfo.getColumn());
        int randomNumberIndex = 0;
        for (Integer number = 1; number <= wheelNumber; number++) {
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
            wheels.add(WheelDo.<SymbolDo>builder()
                    .index(number)
                    .symbolArr(symbolArr)
                    .build());
        }
        return wheels;
    }

    public Map<String, List<BigDecimal>> getProbabilisticConfig(GamePlayDo gamePlay, boolean isFreeSpinsFlag) {
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

    public  MariaMakilingGameRecordDo buildGameRecord(Long id,
                                                             Long parentId,
                                                             Long betRecordId,
                                                             Long batchId,
                                                             WheelDo<SymbolDo> wheel,
                                                             RewardResultDo<SymbolDo> rewardResult,
                                                             List<Long> freeSpinGameRecordIds) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return MariaMakilingGameRecordDo.builder()
                .id(id)
                .parentId(parentId)
                .betRecordId(betRecordId)
                .batchId(batchId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(wheel.getIndex())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(freeSpinGameRecordIds)
                .build();
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
