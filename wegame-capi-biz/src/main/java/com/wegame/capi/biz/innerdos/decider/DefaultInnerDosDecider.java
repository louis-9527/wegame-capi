package com.wegame.capi.biz.innerdos.decider;


import com.wegame.capi.biz.innerdos.context.DefaultInnerDosContext;
import com.wegame.capi.biz.innerdos.context.InnerDosContext;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.innerdos.GameInnerDosTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.utils.GameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 默认内部干预决策器
 */
@Slf4j
@Component("defaultInnerDosDecider")
public class DefaultInnerDosDecider implements InnerDosDecider<DefaultInnerDosContext> {

    private static final String NO_REWARD_TEMPLATE_RANGE_NAME = "0_0";

    @Autowired
    private RandomService randomService;

    @Override
    public GameTypeEnum gameType() {
        // 默认返回null
        return null;
    }

    @Override
    public InnerDosDecideResult decide(DefaultInnerDosContext context) {
        // 判断是否是新人
        boolean newPlayer = context.isNewPlayer();
        if (newPlayer) {
            return this.decideForNewPlayer(context);
        } else {
            return this.decideForNormal(context);
            // 非新人，走常态干预，交给rtp决策
            // 如果没有中奖：交给空盘干预处理
        }
    }

    /**
     * 新人干预
     *
     * @param context context
     * @return result
     */
    private InnerDosDecideResult decideForNewPlayer(DefaultInnerDosContext context) {
        // 1.获取标准rtp模版倍率配置
        Map<String, BigDecimal> templateRangeMap = context.getTemplateRangeMap(GameInnerDosTypeEnum.NORMAL_DOS, context.getBetDo().getBetSubType());
        // 2.随机权重
        int weightSum = templateRangeMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add).multiply(new BigDecimal(Constants.CONST_100)).intValue();
        int randomInt = randomService.randomInt(1, weightSum);
        // 3.计算实际权重
        Integer actualWeight = this.calcActualWeight(templateRangeMap, randomInt, context.getInnerDosCondition().getNewPlayerCondition().getDosCoefficient());
        // 4.获取模版倍率范围区间
        String multipleRange = GameUtil.randomKey(templateRangeMap, Constants.CONST_100, actualWeight);
        // 5.是否中奖
        if (NO_REWARD_TEMPLATE_RANGE_NAME.equals(multipleRange) && context.triggerNewPlayerEmptyDisk()) {
            // 5.1 如果没有中奖 && 触发了空盘限制，则返回最低倍率区间
            return InnerDosDecideResult.builder().templateIdRange(new ArrayList<>(templateRangeMap.keySet()).get(0)).build();
        }
        // 6.返回
        return InnerDosDecideResult.builder().templateIdRange(multipleRange).build();
    }

    public Integer calcActualWeight(Map<String, BigDecimal> templateRangeMap, Integer randomWeight, BigDecimal dosCoefficient) {
        //
        Integer weightSum = templateRangeMap.values().stream().reduce(0, Integer::sum);
        // 随机一个权重
        int randomWeight = randomService.randomInt(1, weightSum);

        // 根据随机权重计算一个实际的权重
        int actualWeight = 0;

        // 不让倾斜之后的权重超过权重总和
        return actualWeight > weightSum ? weightSum : actualWeight;
    }

    /**
     * 常态干预(非新人)
     *
     * @param context context
     * @return result
     */
    private InnerDosDecideResult decideForNormal(DefaultInnerDosContext context) {
        // 1.获取标准rtp模版倍率配置
        Map<String, BigDecimal> templateRangeMap = context.getTemplateRangeMap(GameInnerDosTypeEnum.NORMAL_DOS, context.getBetDo().getBetSubType());
        // 2.随机权重
        int weightSum = templateRangeMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add).multiply(new BigDecimal(Constants.CONST_100)).intValue();
        int randomInt = randomService.randomInt(1, weightSum);
        // 3.计算实际权重
        Integer actualWeight = this.calcActualWeight(templateRangeMap, randomInt, context.getInnerDosCondition().getNewPlayerCondition().getDosCoefficient());
        // 4.获取模版倍率范围区间
        String multipleRange = GameUtil.randomKey(templateRangeMap, Constants.CONST_100, actualWeight);
        // 5.是否中奖
        if (NO_REWARD_TEMPLATE_RANGE_NAME.equals(multipleRange) && context.triggerNewPlayerEmptyDisk()) {
            // 5.1 如果没有中奖 && 触发了空盘限制，则返回最低倍率区间
            return InnerDosDecideResult.builder().templateIdRange(new ArrayList<>(templateRangeMap.keySet()).get(0)).build();
        }
        // 6.返回
        return InnerDosDecideResult.builder().templateIdRange(multipleRange).build();
    }
}
