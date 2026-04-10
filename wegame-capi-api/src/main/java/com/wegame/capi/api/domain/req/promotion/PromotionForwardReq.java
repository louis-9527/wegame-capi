package com.wegame.capi.api.domain.req.promotion;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PromotionForwardReq extends BaseReq {
    private static final long serialVersionUID = 1L;
    /**
     * 业务参数map
     */
    private Map<String, Object> data;

    public Map<String, Object> getParamesMap() {
        Map<String, Object> map = new HashMap<>();
        if (MapUtils.isNotEmpty(data)) {
            map.putAll(data);
        }
        map.put("pId", super.getPId());
        map.put("channelId", super.getChannelId());
        map.put("playerName", super.getPlayerName());
        map.put("hash", super.getHash());
        map.put("refId", super.getRefId());
        map.put("createTime", super.getCreateTime());
        return map;
    }
}
