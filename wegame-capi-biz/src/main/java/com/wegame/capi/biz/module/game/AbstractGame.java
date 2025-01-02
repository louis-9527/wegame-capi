package com.wegame.capi.biz.module.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.*;
import com.wegame.capi.biz.domain.game.RewardHandleDo;
import com.wegame.capi.biz.domain.game.RewardResultDo;
import com.wegame.capi.biz.module.reward.RewardHandler;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class AbstractGame implements Game {

    protected abstract RewardHandler getRewardHandler();


    //    /**
    //     * 获取概率配置
    //     *
    //     * @param gamePlay
    //     * @return
    //     */
    //    protected abstract Map<String, List<BigDecimal>> getProbabilisticConfig(GamePlayDo gamePlay);
    //
    //    /**
    //     * 获取赔率配置
    //     *
    //     * @param gamePlay
    //     * @return
    //     */
    //    protected abstract Map<String, Map<String, BigDecimal>> getOddsConfig(GamePlayDo gamePlay);

    /**
     * 生成滚轮
     *
     * @param gamePlay
     * @return
     */
    protected abstract List<WheelDo> generate(GamePlayDo gamePlay);

    public GamePlayResultDo play(GamePlayDo gamePlay) {
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        // 生成-游戏滚轮列表
        List<WheelDo> wheelList = generate(gamePlay);
        // 判断-滚轮中奖结果
        List<WheelRewardDo> wheelRewardList = wheelList.stream().map(wheel -> {
            RewardResultDo rewardResult = getRewardHandler().handle(RewardHandleDo.builder()
                    .gameType(gameInfo.getGameType())
                    .betType(gamePlay.getBetType())
                    .betSubType(gamePlay.getBetSubType())
                    .betAmount(gamePlay.getBetAmount())
                    .wheel(wheel).build());
            return WheelRewardDo.builder()
                    .wheelIndex(wheel.getIndex())
                    .wheel(wheel)
                    .rewardResult(rewardResult)
                    .build();
        }).collect(Collectors.toList());
        // 返回-结果信息
        return GamePlayResultDo.builder()
                .gameType(gameInfo.getGameType())
                .gameInfo(gameInfo)
                .wheelRewardList(wheelRewardList)
                .build();
    }


    public String randomOneSymbol(int cellNo, Map<String, List<BigDecimal>> probabilisticConfigMap) {
        Map<String, Integer> map = new HashMap<>();
        probabilisticConfigMap.entrySet().forEach(item -> {
            int val = (item.getValue().get(cellNo).multiply(new BigDecimal(100))).intValue();
            if (val > 0) {
                map.put(item.getKey(), val);
            }
        });
        Integer sum = map.values()
                .stream()
                .reduce(0, Integer::sum);
        AssertUtil.assertTrue(sum == 10000, ErrorCode.ERR_PARAM, "Not Support Game");
        Random random = new Random();
        // 生成一个1到10000之间的随机整数
        int randomNumber = 1 + random.nextInt(10000);
        int startVal = 0;
        for (Map.Entry<String, Integer> entry : CollectionUtils.emptyIfNull(map.entrySet())) {
            if (randomNumber > startVal && randomNumber <= startVal + entry.getValue()) {
                return entry.getKey();
            } else {
                startVal += entry.getValue();
            }
        }
        throw new BusinessException(ErrorCode.ERR_INTER);
    }
}
