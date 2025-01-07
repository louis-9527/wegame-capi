package com.wegame.capi.biz.module.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.reward.RewardHandler;
import com.wegame.capi.biz.util.MariaMakilingGameInfoUtil;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import com.wegame.capi.infrastructure.config.nacos.NacosMariaMakilingConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.RewardHandlerBuilder.build;
import static com.wegame.capi.biz.util.MariaMakilingGameInfoUtil.MARIA_MAKILING_SCATTER;
import static com.wegame.capi.biz.util.SymbolRandomUtil.randomOneSymbol;

@Slf4j
@Component
public class MariaMakilingGame extends AbstractGame<MariaMakilingGameRecordDo> {

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
    public RewardHandler getRewardHandler() {
        return mariaMakilingRewardHandler;
    }

    @Override
    public List<WheelDo> generate(GamePlayDo gamePlay) {
        GameInfoDo gameInfo = getGameInfo();
        Map<String, List<BigDecimal>> probabilisticConfig = getProbabilisticConfig(gamePlay);
        if (gamePlay.isFreeSpinsFlag()) {
            return multiWheelGenerate(gameInfo, probabilisticConfig);
        } else {
            // 根据投注类型：生成游戏滚轮
            switch (gamePlay.getBetType()) {
                case NORMAL_BET:
                    return Collections.singletonList(normalWheelGenerate(gameInfo, probabilisticConfig));
                case BUY_BET:
                    return Collections.singletonList(freeSpinsWheelGenerate(gameInfo, probabilisticConfig));
                default:
                    throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
            }
        }
    }

    @Override
    public List<MariaMakilingGameRecordDo> getRewardResult(GameInfoDo gameInfo, GamePlayDo gamePlay, List<WheelDo> wheelList) {
        return Optional.ofNullable(wheelList).orElse(Collections.emptyList()).stream().map(wheel -> {
            RewardResultDo rewardResult = getRewardHandler().handle(build(gameInfo, gamePlay, wheel));
            return MariaMakilingGameRecordDo.builder()
                    .index(wheel.getIndex())
                    .wheel(wheel)
                    .rewardResult(rewardResult)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 正常滚轮生成：生成逻辑无任何限制
     *
     * @param gameInfo 游戏信息
     * @return 滚轮
     */
    private WheelDo normalWheelGenerate(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        SymbolDo[][] symbolArr = new SymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
        for (int i = 0; i < gameInfo.getRow(); i++) {
            for (int j = 0; j < gameInfo.getColumn(); j++) {
                String symbolId = randomOneSymbol(j, probabilisticConfig);
                SymbolDo symbolDo = gameInfo.getSymbolById(symbolId);
                AssertUtil.assertNotNull(symbolDo, ErrorCode.ERR_INTER);
                symbolArr[i][j] = symbolDo;
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
    private WheelDo freeSpinsWheelGenerate(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        WheelDo wheelDo = normalWheelGenerate(gameInfo, probabilisticConfig);
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
     * 生成多个滚轮
     *
     * @param gameInfo 游戏信息
     * @return 滚轮列表
     */
    private List<WheelDo> multiWheelGenerate(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        int wheelNumber = 10 + random.nextInt(15 - 10 + 1);
        List<WheelDo> wheels = new ArrayList<>();
        WheelDo preWheel = null;
        for (int number = 1; number <= wheelNumber; number++) {
            SymbolDo[][] symbolArr;
            if (Objects.isNull(preWheel)) {
                symbolArr = new SymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
            } else {
                symbolArr = preWheel.copy().getSymbolArr();
            }
            for (int i = 0; i < gameInfo.getRow(); i++) {
                for (int j = 0; j < gameInfo.getColumn(); j++) {
                    if (Objects.isNull(symbolArr[i][j]) || !Objects.equals(SymbolTypeEnum.WILD, symbolArr[i][j].getSymbolType())) {
                        String symbolId = randomOneSymbol(j, probabilisticConfig);
                        SymbolDo symbolDo = gameInfo.getSymbolById(symbolId);
                        AssertUtil.assertNotNull(symbolDo, ErrorCode.ERR_INTER);
                        symbolArr[i][j] = symbolDo;
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
            return nacosMariaMakilingConfig.getFreeProbabilisticConfig();
        } else {
            switch (gamePlay.getBetSubType()) {
                case NORMAL_BET:
                case BUY_BET:
                    return nacosMariaMakilingConfig.getCommonProbabilisticConfig();
                case FREE_SPINS_NORMAL_BET:
                case FREE_SPINS_BUY_BET:
                    return nacosMariaMakilingConfig.getFreeProbabilisticConfig();
                default:
                    throw new BusinessException(ErrorCode.ERR_PARAM, "NOT SUPPORTED");
            }
        }
    }
}
