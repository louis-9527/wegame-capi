package com.wegame.capi.biz.service.template.es;

import com.wegame.capi.biz.domain.biz.operation.BetRecordTemplateDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import com.wegame.capi.dao.repo.domain.MalakasBetRecordTemplatePo;
import com.wegame.capi.dao.repo.MalakasBetRecordTemplateRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BetRecordTemplateEsService extends AbstractService {

    @Autowired
    MalakasBetRecordTemplateRepo malakasBetRecordTemplateRepo;

    public Long add(MalakasBetRecordTemplatePo po, TemplateTypeEnum templateTypeEnum) {
        // todo 通用性改造
//        MalakasBetRecordTemplatePo po = new MalakasBetRecordTemplatePo();
//        po.setId(1000L);
//        po.setBetType(BetTypeEnum.BUY_BET.getCode());
//        po.set
        malakasBetRecordTemplateRepo.save(po);
        return po.getId();
    }

    public MalakasBetRecordTemplatePo getById(Long id) {
        return malakasBetRecordTemplateRepo.findById(id).get();
    }
}
