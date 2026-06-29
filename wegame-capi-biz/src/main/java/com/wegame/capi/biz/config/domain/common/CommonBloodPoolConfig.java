package com.wegame.capi.biz.config.domain.common;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommonBloodPoolConfig implements Serializable {
    /**
     * 启用/禁用
     */
    private boolean enable = false;

    /**
     * 平台id:platformId;
     * 渠道id: channelId
     * Map<platformId, List<channelId>>
     */
    private Map<String, List<String>> supportMap;

    public boolean isSupport(String platformId, String channelId) {
        if (MapUtils.isEmpty(supportMap)) {
            return false;
        }
        List<String> channelIds = supportMap.get(platformId);
        return CollectionUtils.isNotEmpty(channelIds) && channelIds.contains(channelId);
    }

    public void validate() {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(supportMap), ErrorCode.ERR_PARAM, "supportMap is empty");
    }

}
