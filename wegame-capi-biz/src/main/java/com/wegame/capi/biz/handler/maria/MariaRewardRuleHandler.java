package com.wegame.capi.biz.handler.maria;

import com.wegame.capi.biz.domain.game.CoordinateDo;
import com.wegame.capi.biz.domain.game.RewardRuleDo;
import com.wegame.capi.biz.domain.game.RewardRuleResultDo;
import com.wegame.capi.biz.handler.RewardRuleHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component("mariaMakilingRewardRuleHandler")
public class MariaRewardRuleHandler implements RewardRuleHandler {
    @Override
    public RewardRuleResultDo handle(RewardRuleDo rewardRuleDo) {
        // 中奖线计算
        // 第一列中奖元素映射
//        Map<Long, CoordinateDo>
        //




        return null;
    }
}
