package com.wegame.capi.biz.module.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.biz.domain.GameInfoDo;
import com.wegame.capi.biz.domain.GamePlayDo;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.domain.WheelDo;
import com.wegame.capi.biz.module.reward.RewardHandler;
import com.wegame.capi.biz.util.MariaMakilingGameInfoUtil;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import com.wegame.capi.infrastructure.config.nacos.NacosMariaMakilingConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static com.wegame.capi.biz.util.MariaMakilingGameInfoUtil.MARIA_MAKILING_SCATTER;
import static com.wegame.capi.biz.util.RandomSymbolUtil.randomOneSymbol;

@Slf4j
@Component
public class MariaMakilingGame extends AbstractGame {

    private static final Random random = new Random();
    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    @Autowired
    RewardHandler mariaMakilingRewardHandler;
    @Autowired
    NacosMariaMakilingConfig nacosMariaMakilingConfig;

    @Override
    public GameInfoDo getGameInfo() {
        return MariaMakilingGameInfoUtil.MARIA_MAKILING_GAME_INFO;
    }

    @Override
    protected RewardHandler getRewardHandler() {
        return mariaMakilingRewardHandler;
    }

    @Override
    public List<WheelDo> generate(GamePlayDo gamePlay) {
        GameInfoDo gameInfo = getGameInfo();
        Map<String, List<BigDecimal>> probabilisticConfig = getProbabilisticConfig(gamePlay);
        if (gamePlay.isFreeSpinsFlag()) {
            return multiGenerate(gameInfo, probabilisticConfig);
        } else {
            // 根据投注类型：生成游戏滚轮
            switch (gamePlay.getBetSubType()) {
                case NORMAL_BET:
                    return Arrays.asList(commonGenerate(gameInfo, probabilisticConfig));
                case BUY_FREE_SPINS_BET:
                case NON_BUY_FREE_SPINS_BET:
                    return Arrays.asList(scatterGenerate(gameInfo, probabilisticConfig));
                default:
                    throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
            }
        }
    }

    /**
     * 生成游戏滚轮：生成逻辑无任何限制
     *
     * @param gameInfo 游戏信息
     * @return 游戏结果
     */
    private WheelDo commonGenerate(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        SymbolDo[][] symbolArr = new SymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
        for (int i = 0; i < gameInfo.getRow(); i++) {
            for (int j = 0; j < gameInfo.getColumn(); j++) {
                String symbolId = randomOneSymbol(i, probabilisticConfig);
                symbolArr[i][j] = gameInfo.getSymbolById(symbolId);
            }
        }
        return WheelDo.builder()
                .index(WHEEL_DEFAULT_INDEX)
                .symbolArr(symbolArr)
                .build();
    }

    /**
     * 生成游戏滚轮：中奖Scatter
     *
     * @param gameInfo
     * @return
     */
    private WheelDo scatterGenerate(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        WheelDo wheelDo = commonGenerate(gameInfo, probabilisticConfig);
        SymbolDo[][] symbolArr = wheelDo.getSymbolArr();
        // 检查1，3，5列是否有Scatter，如果不存在，则1，3，5列各自随机一行的的元素设置为scatter
        List<Integer> scatterCellList = Arrays.asList(0, 2, 4);
        for (Integer cell : scatterCellList) {
            boolean hasScatter = false;
            for (int i = 0; i < gameInfo.getRow(); i++) {
                SymbolDo symbol = symbolArr[i][cell];
                if (Objects.equals(SymbolTypeEnum.SCATTER, symbol.getSymbolType())) {
                    hasScatter = true;
                    break;
                }
            }
            if (!hasScatter) {
                // 生成一个0到行之间的随机整数
                int randomRow = random.nextInt(gameInfo.getRow());
                symbolArr[randomRow][cell] = MARIA_MAKILING_SCATTER;
            }
        }
        return WheelDo.builder()
                .index(WHEEL_DEFAULT_INDEX)
                .symbolArr(symbolArr)
                .build();
    }

    /**
     * 生成游戏滚轮：多个
     *
     * @param gameInfo
     * @return
     */
    private List<WheelDo> multiGenerate(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        int wheelNumber = 10 + random.nextInt(15 - 10 + 1);
        List<WheelDo> wheels = new ArrayList<>();
        WheelDo pre = null;
        for (int times = 0; times < wheelNumber; times++) {
            SymbolDo[][] symbolArr;
            if (Objects.isNull(pre)) {
                symbolArr = new SymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
            } else {
                symbolArr = pre.copy().getSymbolArr();
            }
            for (int i = 0; i < gameInfo.getRow(); i++) {
                for (int j = 0; j < gameInfo.getColumn(); j++) {
                    if (Objects.isNull(symbolArr[i][j]) || !Objects.equals(SymbolTypeEnum.WILD, symbolArr[i][j].getSymbolType())) {
                        String symbolId = randomOneSymbol(i, probabilisticConfig);
                        SymbolDo symbolDo = gameInfo.getSymbolById(symbolId);
                        symbolArr[i][j] = symbolDo;
                    }
                }
            }
            WheelDo wheelDo = WheelDo.builder()
                    .index(times + 1)
                    .symbolArr(symbolArr)
                    .build();
            wheels.add(wheelDo);
            pre = wheelDo;
        }
        return wheels;
    }

    public Map<String, List<BigDecimal>> getProbabilisticConfig(GamePlayDo gamePlay) {
        if (gamePlay.isFreeSpinsFlag()) {
            return nacosMariaMakilingConfig.getFreeProbabilisticConfig();
        } else {
            switch (gamePlay.getBetSubType()) {
                case NORMAL_BET:
                    return nacosMariaMakilingConfig.getCommonProbabilisticConfig();
                case NON_BUY_FREE_SPINS_BET:
                case BUY_FREE_SPINS_BET:
                    return nacosMariaMakilingConfig.getFreeProbabilisticConfig();
                default:
                    throw new BusinessException(ErrorCode.ERR_PARAM, "NOT SUPPORTED");
            }
        }
    }
}
