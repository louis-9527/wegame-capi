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
import com.wegame.capi.biz.builder.req.RedeemRewardReqBuilder;
import com.wegame.capi.biz.converter.promotion.StartCardUseRecordConvert;
import com.wegame.capi.biz.domain.biz.promotion.RedeemRewardResultDo;
import com.wegame.capi.biz.domain.biz.promotion.StarCardUseRecordDo;
import com.wegame.capi.biz.service.AbstractService;
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

import static com.wegame.capi.biz.converter.promotion.StartCardUseRecordConvert.doToPo;

/**
 * 星卡service
 */
@Service
public class StarCardUseRecordService extends AbstractService {

    @Autowired
    BagApi bagApi;

    public Long add(StarCardUseRecordDo useRecordDo, GameTypeEnum gameType) {
        StarCardUseTypeEnum starCardUseType = StarCardUseTypeEnum.valueOfGameType(gameType);
        AssertUtil.assertNotNull(starCardUseType, ErrorCode.ERR_INTER);
        StarCardUseRecordPo poToInsert = doToPo(useRecordDo, OptEnum.ADD);
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
    public RedeemRewardResultDo use(String rewardId, Long payoutAmount, Long betWithPayoutRefId) {
        CommonRes<RedeemRewardRes> res = bagApi.redeemReward(RedeemRewardReqBuilder.build(rewardId, payoutAmount, betWithPayoutRefId));
        return RedeemRewardResultDo.builder()
                .success(res.isSuc())
                .status(res.getStatus())
                .message(res.getMessage())
                .build();
    }
}
