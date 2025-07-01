package com.wegame.capi.biz.module.base.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.common.utils.GameUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 抽象游戏
 *
 * @param <R>
 */
@Slf4j
public abstract class AbstractWheelChartGame<R extends ToVoConverter> extends AbstractGame<R> {


    /**
     * 随机生成symbolId列表
     *
     * @param sourceReelWheelChart 源滚轮轮代表
     * @param resultListCount      期望生成数量
     * @return list
     */
    protected List<String> randomCircularGenSymbolIdList(List<String> sourceReelWheelChart, int resultListCount) {
        AssertUtil.assertNotEmpty(sourceReelWheelChart, ErrorCode.ERR_INTER, "sourceReelWheelChart is empty");
        AssertUtil.assertTrue(resultListCount > 0, ErrorCode.ERR_INTER, "resultListCount is empty");
        // 生成随机数：从当前随机数开始，顺序循环取row个元素id
        int reelSymbolStartIndex = randomService.randomInt(0, sourceReelWheelChart.size() - 1);
        // 取出当前列的元素id 进行元素填充
        return GameUtil.circularGetList(sourceReelWheelChart, reelSymbolStartIndex, resultListCount);
    }


}
