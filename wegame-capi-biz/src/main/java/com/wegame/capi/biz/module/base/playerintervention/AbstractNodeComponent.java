package com.wegame.capi.biz.module.base.playerintervention;

import cn.hutool.core.util.NumberUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.intervention.InterventionConfig;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionContextDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionParamDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionResultDo;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.InterventionChainIdTypeEnum;
import com.wegame.capi.common.enums.RepeatTypeEnum;
import com.yomahub.liteflow.core.NodeComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractNodeComponent<T extends InterventionConfig> extends NodeComponent {
    @Autowired
    private RandomService randomService;

    /**
     * 不执行条件（或）：
     * 1.同一个链上一次干预已经有明确结果
     * 2.上一次结果优先级大于当前优先级
     *
     * @return true/false
     */
    protected boolean canContinue(PlayerInterventionParamDo paramDo, T config, PlayerInterventionContextDo contextDo) {
        AssertUtil.assertNotNull(contextDo, ErrorCode.ERR_INTER, "PlayerInterventionContextDo is null");
        if (Objects.isNull(contextDo.getPreCmpResult())) {
            return true;
        }

        String chainId = this.getChainId();
        if (Objects.nonNull(contextDo.getPreCmpResult())) {
            // 上一个结果的链优先级不能大于当前链优先级
            InterventionChainIdTypeEnum chainIdType = InterventionChainIdTypeEnum.valueOfConfigId(this.getChainId());
            AssertUtil.assertNotNull(chainIdType, ErrorCode.ERR_INTER, "chainIdType is null of " + this.getChainId());
            AssertUtil.assertTrue(contextDo.getPreCmpResult().getChainSort() >= chainIdType.getDefaultSort(), ErrorCode.ERR_INTER, "chainId sort error of " + this.getChainId());
            if (Objects.equals(chainId, contextDo.getPreCmpResult().getChainId())) {
                return false;
            }
            String sortStr = this.getTag();
            AssertUtil.assertTrue(NumberUtil.isInteger(sortStr), ErrorCode.ERR_PARAM, "rule sort is not integer");
            int cmpSort = Integer.parseInt(sortStr);
            if (contextDo.getPreCmpResult().getCmpSort() > cmpSort) {
                return false;
            }
        }

        return true;
    }

    protected abstract boolean triggerCondition(PlayerInterventionParamDo paramDo, T config, PlayerInterventionContextDo contextDo);

    protected abstract boolean triggerProbability(PlayerInterventionParamDo paramDo, T config, PlayerInterventionContextDo contextDo);

    protected abstract boolean repeatCondition(PlayerInterventionParamDo paramDo, T config, PlayerInterventionContextDo contextDo);

    protected abstract void intervention(PlayerInterventionParamDo paramDo, T config, PlayerInterventionContextDo contextDo);

    public void doProcess(PlayerInterventionParamDo paramDo, T config, PlayerInterventionContextDo contextDo) throws Exception {
        contextDo.setCurrentCmpResultPoint(null);
        if (!this.canContinue(paramDo, config, contextDo)) {
            return;
        }
        if (!this.triggerCondition(paramDo, config, contextDo)) {
            return;
        }
        if (!this.triggerProbability(paramDo, config, contextDo)) {
            return;
        }
        if (!this.repeatCondition(paramDo, config, contextDo)) {
            return;
        }
        this.intervention(paramDo, config, contextDo);
    }

    @Override
    public void process() throws Exception {
        PlayerInterventionParamDo paramDo = this.getRequestData();
        T config = this.getCmpData(this.beanClass());
        PlayerInterventionContextDo contextDo = this.getContextBean(PlayerInterventionContextDo.class);
        this.doProcess(paramDo, config, contextDo);
        // point 组件内失效
        contextDo.setCurrentCmpResultPoint(null);
    }

    @SuppressWarnings("unchecked")
    private Class<T> beanClass() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) superClass.getActualTypeArguments()[0];
    }

    protected boolean commonRepeatCondition(PlayerInterventionParamDo paramDo, T config, PlayerInterventionContextDo contextDo) {
        RepeatTypeEnum repeatTypeEnum = RepeatTypeEnum.valueOfCode(config.getRepeatType());
        AssertUtil.assertNotNull(repeatTypeEnum, ErrorCode.ERR_PARAM, "repeatType is null");
        UserMetricInfoDo userMetricInfoDo = paramDo.getUserMetricInfoDo();
        AssertUtil.assertNotNull(userMetricInfoDo, ErrorCode.ERR_PARAM, "userMetricInfoDo is null");
        Map<String, Long> interventionRecord = userMetricInfoDo.getInterventionRecord();
        if (Objects.isNull(interventionRecord) || !interventionRecord.containsKey(this.getNodeId())) {
            return true;
        } else {
            return repeatTypeEnum.assertTime(interventionRecord.get(this.getNodeId()), null);
        }
    }

    protected boolean fixedTriggerProbability(PlayerInterventionParamDo paramDo, T config, PlayerInterventionContextDo contextDo) {
        return this.canTrigger(config.getTriggerProbability());
    }

    protected boolean canTrigger(BigDecimal triggerProbability) {
        int proInt = triggerProbability.multiply(new BigDecimal(Constants.CONST_10000)).intValue();
        if (proInt<=0 || proInt>=Constants.CONST_10000){
            return true;
        }
        return randomService.randomInt(Constants.CONST_1, Constants.CONST_10000) <= proInt;
    }

    protected void buildInterventionResult(PlayerInterventionParamDo paramDo,
                                           T config,
                                           PlayerInterventionContextDo contextDo,
                                           TemplateMultipleRange templateMultipleRange,
                                           BetRecordTemplateEsDo betRecordTemplateEsDo,
                                           Map<String, Object> extInfo) {
        InterventionChainIdTypeEnum chainIdType = InterventionChainIdTypeEnum.valueOfConfigId(this.getChainId());
        AssertUtil.assertNotNull(chainIdType, ErrorCode.ERR_INTER, "chainIdType is null");
        PlayerInterventionResultDo currentResult = PlayerInterventionResultDo.builder()
                .chainId(this.getChainId())
                .chainSort(chainIdType.getDefaultSort())
                .componentId(this.getNodeId())
                .cmpSort(Integer.valueOf(this.getTag()))
                .rtpName(config.getRtpName())
                .backstopRtpName(config.getBackstopRtpName())
                .templateMultipleRange(templateMultipleRange)
                .betRecordTemplateEsDo(betRecordTemplateEsDo)
                .build();
        contextDo.setPreCmpResult(currentResult);
        contextDo.setCurrentCmpResultPoint(currentResult);
        contextDo.addChainResult(currentResult);
    }


}
