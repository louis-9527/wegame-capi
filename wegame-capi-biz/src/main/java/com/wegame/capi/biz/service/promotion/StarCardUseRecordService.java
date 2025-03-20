package com.wegame.capi.biz.service.promotion;

import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.promotion.RedeemRewardReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.promotion.RedeemRewardRes;
import com.wegame.capi.api.promotion.BagApi;
import com.wegame.capi.biz.builder.StarCardUseRecordQueryBuilder;
import com.wegame.capi.biz.converter.promotion.StartCardUseRecordConvert;
import com.wegame.capi.biz.domain.biz.promotion.RedeemRewardResultDo;
import com.wegame.capi.biz.domain.biz.promotion.StarCardUseRecordDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.StarCardUseTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.dao.mapper.domain.star.StarCardUseRecordPo;
import com.wegame.capi.infrastructure.config.MongodbTemplateConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 星卡service
 */
@Service
public class StarCardUseRecordService {

    @Autowired // todo fix @tutu
    public SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    Map<String, MongoTemplate> mongoTemplateMap;
    @Autowired
    BagApi bagApi;

    public Long add(StarCardUseRecordDo useRecordDo, GameTypeEnum gameType) {
        StarCardUseTypeEnum starCardUseType = StarCardUseTypeEnum.valueOfGameType(gameType);
        AssertUtil.assertNotNull(starCardUseType, ErrorCode.ERR_INTER);
        StarCardUseRecordPo poToInsert = StartCardUseRecordConvert.doToPo(useRecordDo, OptEnum.ADD);
        StarCardUseRecordPo po = getMongoTemplate(gameType).insert(poToInsert, starCardUseType.getCollectionName());
        return po.getId();
    }

    public Boolean update(StarCardUseRecordDo useRecordDo, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(useRecordDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(useRecordDo.getId(), ErrorCode.ERR_PARAM, "request param \'id\' is null");
        Criteria criteria = new Criteria();
        criteria.and("_id").is(useRecordDo.getId());
        Query query = new Query(criteria);
        UpdateResult result = getMongoTemplate(gameType).updateFirst(query,
                StarCardUseRecordQueryBuilder.buildMongoUpdate(useRecordDo),
                StarCardUseRecordPo.class,
                gameType.getBetRecordCollectionName());
        return result.getModifiedCount() > 0;
    }

    /**
     * 核销-使用星卡
     */
    public RedeemRewardResultDo use(String rewardId, Long payoutAmount, Long betWIthPayoutRefId) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        RedeemRewardReq redeemRewardReq = RedeemRewardReq.builder()
                .playerName(userInfo.getPlayerName())
                .pId(userInfo.getPId())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .channelId(userInfo.getChannelId())
                .createTime(DateUtil.toUTC(new Date()))
                .rewardId(rewardId)
                .payoutAmount(AmountUtil.toBigDecimal(payoutAmount))
                .betWIthPayoutRefId(String.valueOf(betWIthPayoutRefId))
                .build();
        CommonRes<RedeemRewardRes> res = bagApi.redeemReward(redeemRewardReq);
        return RedeemRewardResultDo.builder()
                .success(res.isSuc())
                .status(res.getStatus())
                .message(res.getMessage())
                .build();
    }

    private MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
        MongoTemplate template = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MongodbTemplateConfig.MONGOTEMPLATE);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        return template;
    }
}
