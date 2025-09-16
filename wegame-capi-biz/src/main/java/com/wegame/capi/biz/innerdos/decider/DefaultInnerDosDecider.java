package com.wegame.capi.biz.innerdos.decider;


import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.metrics.GameMetricsDo;
import com.wegame.capi.biz.innerdos.context.DefaultInnerDosContext;
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
import java.util.Objects;

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
        int weightSum = templateRangeMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add).multiply(new BigDecimal(Constants.CONST_10000)).intValue();
        int randomWeight = randomService.randomInt(1, weightSum);
        // 3.计算实际权重
        String normalRtpType = context.getTemplateName(GameInnerDosTypeEnum.NORMAL_DOS);
        Integer actualWeight = this.calcActualWeight(new BigDecimal(normalRtpType),
                context.getUserCurrentRtp(),
                context.getNewPlayerDosCoefficient(),
                randomWeight,
                weightSum);
        // 4.获取模版倍率范围区间
        String multipleRange = GameUtil.randomKey(templateRangeMap, Constants.CONST_10000, actualWeight);
        // 5.是否中奖
        if (NO_REWARD_TEMPLATE_RANGE_NAME.equals(multipleRange) && context.triggerNewPlayerEmptyDisk()) {
            // 5.1 如果没有中奖 && 触发了空盘限制，则返回最低倍率区间
            return InnerDosDecideResult.builder().templateIdRange(new ArrayList<>(templateRangeMap.keySet()).get(0)).build();
        }
        // 6.返回
        return InnerDosDecideResult.builder().templateIdRange(multipleRange).build();
    }

    /**
     * 常态干预(非新人)
     *
     * @param context context
     * @return result
     */
    private InnerDosDecideResult decideForNormal(DefaultInnerDosContext context) {
        // 1.获取当前使用中的大盘常态干预类型
        GameMetricsDo gameMetricsDo = context.getGameMetricsDo();
        AssertUtil.assertNotNull(gameMetricsDo, ErrorCode.ERR_INTER, "gameMetricsDo is null");
        GameInnerDosTypeEnum inUseNormalDosType = GameInnerDosTypeEnum.valueOfCode(gameMetricsDo.getInUseNormalDosType());
        // 2.获取对应的模版倍率配置
        Map<String, BigDecimal> templateRangeMap = context.getTemplateRangeMap(inUseNormalDosType, context.getBetDo().getBetSubType());
        // 3.随机权重
        int weightSum = templateRangeMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add).multiply(new BigDecimal(Constants.CONST_10000)).intValue();
        int randomWeight = randomService.randomInt(1, weightSum);
        // 4.如果是常态干预,则计算实际权重
        Integer actualWeight = randomWeight;
        if (Objects.equals(inUseNormalDosType, GameInnerDosTypeEnum.NORMAL_DOS)) {
            // 4.1计算实际权重
            actualWeight = this.calcActualWeight(new BigDecimal(context.getTemplateName(GameInnerDosTypeEnum.NORMAL_DOS)),
                    context.getUserCurrentRtp(),
                    context.getNormalDosCoefficient(),
                    randomWeight,
                    weightSum);
        }
        // 5.随机获取模版倍率范围区间
        String multipleRange = GameUtil.randomKey(templateRangeMap, Constants.CONST_10000, actualWeight);
        // 6.是否中奖
        if (NO_REWARD_TEMPLATE_RANGE_NAME.equals(multipleRange) && context.triggerNormalPlayerEmptyDisk()) {
            // 6.1 如果没有中奖 && 触发了空盘限制，则返回最低倍率区间
            return InnerDosDecideResult.builder().templateIdRange(new ArrayList<>(templateRangeMap.keySet()).get(0)).build();
        }
        // 7.返回
        return InnerDosDecideResult.builder().templateIdRange(multipleRange).build();
    }

    /**
     * 计算实际rtp
     *
     * @param normalRtpType  标准rtp
     * @param userCurrentRtp 用户当前rtp
     * @param dosCoefficient 干预系数
     * @param randomWeight   随机权重
     * @param weightSum      当前使用模版的权重总和
     * @return 实际权重
     */
    private Integer calcActualWeight(BigDecimal normalRtpType,
                                     BigDecimal userCurrentRtp,
                                     BigDecimal dosCoefficient,
                                     Integer randomWeight,
                                     Integer weightSum) {
        // 1.计算倾斜值.偏斜值 = （标准RTP – 用户当前RTP）* 干预系数
        BigDecimal inclineValue = normalRtpType.subtract(userCurrentRtp).multiply(dosCoefficient);
        // 2.计算实际计算权重.（1+ 偏斜值）* 权重 = 实际使用权重
        int calcActualWeight = BigDecimal.ONE.add(inclineValue).multiply(BigDecimal.valueOf(randomWeight)).intValue();
        // 3.获取实际使用权重.如果calcActualWeight大于总和了则取总和
        return calcActualWeight > weightSum ? weightSum : calcActualWeight;
    }
}
