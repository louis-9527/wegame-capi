package com.wegame.capi.biz.module.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.MariaMakilingConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.reward.RewardHandler;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static com.wegame.capi.biz.helper.MariaMakilingGameHelper.MARIA_MAKILING_GAME_INFO;
import static com.wegame.capi.biz.helper.MariaMakilingGameHelper.MARIA_MAKILING_SCATTER;
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
    public RewardHandler getRewardHandler() {
        return mariaMakilingRewardHandler;
    }

    @Override
    public List<WheelDo> generate(GamePlayDo gamePlay) {
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

    @Override
    public MariaMakilingGameRecordDo buildGameRecord(WheelDo wheel, RewardResultDo rewardResult) {
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
        for (int colum = 0; colum < gameInfo.getColumn(); colum++) {
            boolean hasScatter = false;
            Map<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(colum, probabilisticConfig);
            for (int row = 0; row < gameInfo.getRow(); ) {
                SymbolEnum symbol = randomOneSymbol(columProbabilisticMap);
                SymbolDo symbolDo = gameInfo.getSymbolById(symbol.getId());
                AssertUtil.assertNotNull(symbolDo, ErrorCode.ERR_INTER);
                if (hasScatter) {
                    if (symbolDo.isScatterSymbol()) {
                        // do nothing
                    } else {
                        symbolArr[row][colum] = symbolDo;
                        row++;
                    }
                } else {
                    symbolArr[row][colum] = symbolDo;
                    row++;
                    if (symbolDo.isScatterSymbol()) {
                        hasScatter = true;
                    }
                }
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
                int randomRow = randomService.randomIntOld(0, gameInfo.getRow() - 1);
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
     *
     * @param gameInfo 游戏信息
     * @return 滚轮列表
     */
    private List<WheelDo> generateMultiWheel(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        int wheelNumber = getWheelNumber();
        List<WheelDo> wheels = new ArrayList<>();
        WheelDo preWheel = null;
        for (int number = 1; number <= wheelNumber; number++) {
            SymbolDo[][] symbolArr;
            if (Objects.isNull(preWheel)) {
                symbolArr = new SymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
            } else {
                symbolArr = preWheel.copy().getSymbolArr();
            }
            for (int colum = 0; colum < gameInfo.getColumn(); colum++) {
                Map<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(colum, probabilisticConfig);
                for (int row = 0; row < gameInfo.getRow(); row++) {
                    if (Objects.isNull(symbolArr[row][colum]) || !Objects.equals(SymbolTypeEnum.WILD, symbolArr[row][colum].getSymbolType())) {
                        SymbolEnum symbol = randomOneSymbol(columProbabilisticMap);
                        SymbolDo symbolDo = gameInfo.getSymbolById(symbol.getId());
                        AssertUtil.assertNotNull(symbolDo, ErrorCode.ERR_INTER);
                        symbolArr[row][colum] = symbolDo;
                    }
                }
            }
            WheelDo wheelDo = WheelDo.builder()
                    .index(number)
                    .symbolArr(symbolArr)
                    .build();
            wheels.add(wheelDo);
            preWheel = wheelDo;
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
        int randomNumber = randomService.randomIntOld(1, 100);
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
