package com.wegame.capi.biz.module.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.reward.RewardHandler;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.common.enums.SymbolEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.RewardHandlerBuilder.build;

/**
 * 抽象游戏
 * @param <R>
 */
public abstract class AbstractGame<R extends ToVoConverter> implements Game<R> {

    @Autowired
    RandomService randomService;

    public abstract RewardHandler getRewardHandler();

    public abstract List<WheelDo> generate(GamePlayDo gamePlay);

    public abstract R buildGameRecord(WheelDo wheel, RewardResultDo rewardResult);

    public List<R> play(GamePlayDo gamePlay) {
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        // 生成-游戏滚轮列表
        List<WheelDo> wheelList = generate(gamePlay);
        // 组装&返回-游戏记录列表
        return Optional.ofNullable(wheelList)
                .orElse(Collections.emptyList())
                .stream()
                .map(wheel -> buildGameRecord(wheel, getRewardHandler().handle(build(gameInfo, gamePlay, wheel))))
                .collect(Collectors.toList());
    }

    public SymbolEnum randomOneSymbol(int cellNo, Map<SymbolEnum, List<BigDecimal>> probabilisticConfigMap) {
        Map<SymbolEnum, Integer> map = new HashMap<>();
        probabilisticConfigMap.entrySet().forEach(item -> {
            int val = (item.getValue().get(cellNo).multiply(new BigDecimal(100))).intValue();
            if (val > 0) {
                map.put(item.getKey(), val);
            }
        });
        Integer sum = map.values()
                .stream()
                .reduce(0, Integer::sum);
        AssertUtil.assertTrue(sum == 10000, ErrorCode.ERR_INTER, "Symbol config error,cellNo:" + cellNo);
        Random random = new Random();
        // 生成一个1到10000之间的随机整数
        int randomNumber = randomService.randomInt(1, 10000);
        int startVal = 0;
        for (Map.Entry<SymbolEnum, Integer> entry : CollectionUtils.emptyIfNull(map.entrySet())) {
            if (randomNumber > startVal && randomNumber <= startVal + entry.getValue()) {
                return entry.getKey();
            } else {
                startVal += entry.getValue();
            }
        }
        throw new BusinessException(ErrorCode.ERR_INTER);
    }
}
