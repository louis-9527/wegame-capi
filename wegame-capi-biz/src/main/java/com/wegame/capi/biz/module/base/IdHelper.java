package com.wegame.capi.biz.module.base;

import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class IdHelper {
    public static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;

    public Map<Long, Long> buildIdMap(List<GameRecordTemplateDo> configRecordList) {
        Map<Long, Long> idMap = new HashMap<>();
        idMap.put(RECORD_DEFAULT_PARENT_ID, RECORD_DEFAULT_PARENT_ID);
        for (GameRecordTemplateDo item : CollectionUtils.emptyIfNull(configRecordList)) {
            if (!idMap.containsKey(item.getId())) {
                idMap.put(item.getId(), generateId());
            }
        }
        return idMap;
    }

    public long generateId() {
        return snowflakeIdGenerator.nextId();
    }
}