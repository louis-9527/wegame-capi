package com.wegame.capi.biz.module.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.biz.domain.GameInfoDo;
import com.wegame.capi.biz.domain.GamePlayDo;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.domain.WheelDo;
import com.wegame.capi.biz.module.probabilistic.strategy.AbstractProbabilisticStrategy;
import com.wegame.capi.biz.module.reward.strategy.RewardHandler;
import com.wegame.capi.biz.util.MariaMakilingGameInfoUtil;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.wegame.capi.biz.util.MariaMakilingGameInfoUtil.MARIA_MAKILING_SCATTER;

@Slf4j
@Component("mariaMakilingGameStrategy")
public class MariaMakilingGame extends AbstractGame {

    private static final Random random = new Random();
    @Autowired
    RewardHandler mariaMakilingRewardHandler;

    @Autowired
    AbstractProbabilisticStrategy mariaMakilingProbabilisticStrategy;

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
        if (gamePlay.isFreeSpinsFlag()) {
            List<WheelDo> wheels = Arrays.asList();
            int wheelNumber = 10 + random.nextInt(15 - 10 + 1);
            for (int i = 0; i < wheelNumber; i++) {
                // todo 控制wild @bruce
                wheels.add(nonScatterGenerate(gameInfo));
            }
        } else {
            // 根据投注类型：生产游戏滚轮
            switch (gamePlay.getBetSubType()) {
                case NORMAL_BET:
                    return Arrays.asList(commonGenerate(gameInfo));
                case BUY_FREE_SPINS_BET:
                case NON_BUY_FREE_SPINS_BET:
                    return Arrays.asList(scatterGenerate(gameInfo));
                default:
                    throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
            }
        }
        return null;
    }

    /**
     * 生成游戏结果：生成逻辑无任何限制
     *
     * @param gameInfo 游戏信息
     * @return 游戏结果
     */
    private WheelDo commonGenerate(GameInfoDo gameInfo) {
        SymbolDo[][] symbolArr = new SymbolDo[gameInfo.getRow()][gameInfo.getCell()];
        for (int i = 0; i < gameInfo.getRow(); i++) {
            for (int j = 0; j < gameInfo.getCell(); j++) {
                String symbolId = mariaMakilingProbabilisticStrategy.randomOneSymbol(gameInfo.getGameType(), i);
                symbolArr[i][j] = gameInfo.getSymbolById(symbolId);
            }
        }
        return WheelDo.builder()
                .symbolArr(symbolArr)
                .build();
    }

    /**
     * 生成游戏结果：中奖Scatter
     *
     * @param gameInfo
     * @return
     */
    private WheelDo scatterGenerate(GameInfoDo gameInfo) {
        WheelDo wheelDo = commonGenerate(gameInfo);
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
                .symbolArr(symbolArr)
                .build();
    }

    /**
     * 生成游戏结果：非Scatter
     *
     * @param gameInfo
     * @return
     */
    private WheelDo nonScatterGenerate(GameInfoDo gameInfo) {
        // 检查0，2，4列是否有Scatter，如果有，则重新生成
        List<Integer> scatterCellList = Arrays.asList(0, 2, 4);
        SymbolDo[][] symbolArr = new SymbolDo[gameInfo.getRow()][gameInfo.getCell()];
        for (int i = 0; i < gameInfo.getRow(); i++) {
            for (int j = 0; j < gameInfo.getCell(); j++) {
                String symbolId = mariaMakilingProbabilisticStrategy.randomOneSymbol(gameInfo.getGameType(), i);
                SymbolDo symbolDo = gameInfo.getSymbolById(symbolId);
                while (scatterCellList.contains(j) && Objects.equals(symbolDo, MARIA_MAKILING_SCATTER)) {
                    symbolId = mariaMakilingProbabilisticStrategy.randomOneSymbol(gameInfo.getGameType(), i);
                    symbolDo = gameInfo.getSymbolById(symbolId);
                }
            }
        }
        return WheelDo.builder()
                .symbolArr(symbolArr)
                .build();
    }
}
