package com.wegame.capi.biz.module.game;

import com.wegame.capi.biz.util.MariaMakilingGameInfoUtil;
import com.wegame.capi.biz.domain.GameInfoDo;
import com.wegame.capi.biz.domain.GamePlayDo;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.module.probabilistic.strategy.AbstractProbabilisticStrategy;
import com.wegame.capi.common.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import lombok.extern.slf4j.Slf4j;
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

    private Random random = new Random();

    @Autowired
    AbstractProbabilisticStrategy mariaMakilingProbabilisticStrategy;

    @Override
    public GameInfoDo getGameInfo() {
        return MariaMakilingGameInfoUtil.MARIA_MAKILING_GAME_INFO;
    }

    @Override
    public SymbolDo[][] generate(GameInfoDo gameInfo, GamePlayDo gamePlay) {
        // 根据投注类型：生产游戏记录结果
        return null;
    }


    /**
     * 生成游戏结果：生成逻辑无任何限制
     *
     * @param gameInfo 游戏信息
     * @return 游戏结果
     */
    private SymbolDo[][] commonGenerate(GameInfoDo gameInfo) {
        SymbolDo[][] ret = new SymbolDo[gameInfo.getRow()][gameInfo.getCell()];
        for (int i = 0; i < gameInfo.getRow(); i++) {
            for (int j = 0; j < gameInfo.getCell(); j++) {
                String symbolId = mariaMakilingProbabilisticStrategy.randomOneSymbol(gameInfo.getGameType(), i);
                ret[i][j] = gameInfo.getSymbolById(symbolId);
            }
        }
        return ret;
    }

    /**
     * 生成游戏结果：中奖Scatter
     *
     * @param gameInfo
     * @return
     */
    private SymbolDo[][] scatterGenerate(GameInfoDo gameInfo) {
        SymbolDo[][] symbolArr = commonGenerate(gameInfo);
        // 检查1，3，5列是否有Scatter，如果不存在，则1，3，5列各自随机一行的的元素设置为scatter
        List<Integer> scatterCellList = Arrays.asList(0, 2, 4);
        for(Integer cell : scatterCellList) {
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
        return symbolArr;
    }




}
