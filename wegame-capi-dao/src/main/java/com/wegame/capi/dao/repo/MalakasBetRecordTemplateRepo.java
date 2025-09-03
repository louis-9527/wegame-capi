package com.wegame.capi.dao.repo;

import com.wegame.capi.dao.repo.domain.MalakasBetRecordTemplatePo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MalakasBetRecordTemplateRepo extends ElasticsearchRepository<MalakasBetRecordTemplatePo, Long> {

}