package com.wegame.capi.biz.module.base.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.domain.res.singlewallet.GetPoolConfigRes;
import com.wegame.capi.biz.config.domain.common.CommonTemplateConfig;
import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.bet.BetBloodPoolControlDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.biz.service.bloodpool.BloodPoolControlService;
import com.wegame.capi.biz.service.template.BetRecordTemplateService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.domain.TemplateMultipleRange;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import com.wegame.capi.infrastructure.helper.LarkHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.wegame.capi.api.SingleWalletConfigApi.RTP_EX_MATCH_WARNING_MSG_FORMAT;
import static com.wegame.capi.common.Constants.*;

/**
 * 抽象游戏模版Play
 */
@Slf4j
public abstract class AbstractGameTemplatePlay<R extends ToVoConverter, T extends BetRecordTemplateEsDo> implements Game<R> {

    @Autowired
    BloodPoolControlService bloodPoolControlService;
    @Autowired
    public BetRecordTemplateService<T> betRecordTemplateService;
    @Autowired
    public RandomService randomService;
    @Autowired
    LarkHelper larkHelper;

    public abstract TemplateGame<R, T> templateGame();

    public T getTemplate(BetDo betDo, GamePlayDo gamePlayDo) {
        // do nothing, subClass override this method
        return null;
    }

    @Override
    public List<R> play(BetDo betDo, GamePlayDo gamePlayDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        T template = getTemplate(betDo, gamePlayDo);
        return templateGame().templatePlay(betDo, gamePlayDo, template);
    }

    public TemplateMultipleRange getTemplateMultipleRange(TreeMap<String, Integer> treeMap) {
        String templateMultipleRange = randomService.randomKeyByWeight(treeMap);
        AssertUtil.assertStrNotEmpty(templateMultipleRange, ErrorCode.ERR_INTER);
        return TemplateMultipleRange.build(templateMultipleRange);
    }

    public TemplateMultipleRange getTemplateMultipleRange(BetDo betDo, GamePlayDo gamePlayDo, CommonTemplateConfig commonTemplateConfig) {
        GetPoolConfigRes.BloodPoolConfig bloodPoolConfig = bloodPoolControlService.matchBloodPoolConfig(betDo, gamePlayDo);
        if (Objects.nonNull(bloodPoolConfig)) {
            // 设置投注上下文
            betDo.setBetBloodPoolControl(BetBloodPoolControlDo.builder()
                    .matchBloodPoolCategory(bloodPoolConfig.getPoolCategory())
                    .matchBloodPoolLevel(bloodPoolConfig.getCurrentLevelNo())
                    .enableFundPool(false) // todo
                    .build());
            try {
                return fetchTemplateMultipleRange(commonTemplateConfig, bloodPoolConfig.getPoolCategory(), bloodPoolConfig.getCurrentLevelNo(), betDo.getBetType());
            } catch (Exception e) {
                betDo.setBetBloodPoolControl(null);
                larkHelper.sendWarningMsg(String.format(RTP_EX_MATCH_WARNING_MSG_FORMAT,
                        bloodPoolConfig.getPoolCategory(),
                        bloodPoolConfig.getCurrentLevelNo(),
                        gamePlayDo.getBetType().getCode(),
                        gamePlayDo.getGameType().getId()));
                log.error("match multipleWeightMap error", e);
                return getDefaultTemplateMultipleRange(betDo, commonTemplateConfig);
            }
        } else {
            return getDefaultTemplateMultipleRange(betDo, commonTemplateConfig);
        }
    }

    public TemplateMultipleRange getDefaultTemplateMultipleRange(BetDo betDo, CommonTemplateConfig commonTemplateConfig) {
        String key = Constants.DEFAULT;
        String subKey = getDefaultRewardControlSubKey(betDo.getBetPromotionType());
        return fetchTemplateMultipleRange(commonTemplateConfig, key, subKey, betDo.getBetType());
    }

    private String getDefaultRewardControlSubKey(BetPromotionTypeEnum betPromotionType) {
        switch (betPromotionType) {
            case NONE:
                return Reward_Config_subkey_DEFAULT;
            case FREE_SPIN:
                return Reward_Config_subkey_FreeSpin;
            case STAR_CARD:
            case STAR_CARD_IN_FREE:
                return Reward_Config_subkey_StarCard;
        }
        throw new BusinessException(ErrorCode.ERR_INTER);
    }

    private TemplateMultipleRange fetchTemplateMultipleRange(CommonTemplateConfig commonTemplateConfig, String key, String subKey, BetTypeEnum betType) {
        Map<String, Integer> multipleWeightMap = commonTemplateConfig.getMultipileWeightMap(key, subKey, betType);
        String templateMultipleRange = randomService.randomKeyByWeight(multipleWeightMap);
        AssertUtil.assertStrNotEmpty(templateMultipleRange, ErrorCode.ERR_INTER);
        return TemplateMultipleRange.build(templateMultipleRange);
    }
}
