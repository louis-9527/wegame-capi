package com.wegame.capi.biz.service.template;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GameRecordTemplateService<T> extends AbstractService {

    public boolean batchAdd(List<T> gameRecordList, TemplateTypeEnum templateTypeEnum, Class<T> tClass) {
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(templateTypeEnum, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(tClass, ErrorCode.ERR_INTER);
        BulkOperations bulkOps = getMongoTemplate(templateTypeEnum.getGameType()).bulkOps(BulkOperations.BulkMode.UNORDERED, tClass, templateTypeEnum.getGameRecordTemplateName());
        bulkOps.insert(gameRecordList);
        bulkOps.execute();
        return true;
    }
}
