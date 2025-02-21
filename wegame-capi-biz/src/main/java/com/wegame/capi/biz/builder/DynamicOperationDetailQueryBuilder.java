package com.wegame.capi.biz.builder;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Objects;

public class DynamicOperationDetailQueryBuilder {


    public static Update buildUpdateStatus(DynamicOperationEventDetailDo detailDo) {

        if (detailDo == null) {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
        Update update = new Update();
        if (Objects.nonNull(detailDo.getBetStatus())) {
            update.set("betStatus", detailDo.getBetStatus().getCode());
        }

        if (Objects.nonNull(detailDo.getUpdateTime())) {
            update.set("updateTime", detailDo.getUpdateTime());
        }

        return update;

    }

}
