package com.wegame.capi.biz.service.template;

import com.wegame.base.CacheConstants;
import com.wegame.capi.biz.builder.template.GameRecordRecordQueryBuilder;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import com.wegame.capi.dao.repository.domain.template.GameRecordTemplatePo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wegame.capi.biz.converter.operation.GameRecordTemplateConvert.poToDoList;

@Service
public class GameRecordTemplateService extends AbstractService {

    @Cacheable(value = CacheConstants.OPERATION_EVENT_CONFIG, key = "#typeEnum.name() + '_' + #betRecordId")
    public List<GameRecordTemplateDo> listByBetRecordId(Long betRecordId, TemplateTypeEnum typeEnum) {
        List<GameRecordTemplatePo> poList = getMongoTemplate(typeEnum.getGameType())
                .find(GameRecordRecordQueryBuilder.buildQuery(betRecordId),
                        GameRecordTemplatePo.class,
                        typeEnum.getGameRecordTemplateCollectionName());
        return poToDoList(poList);
    }
}
