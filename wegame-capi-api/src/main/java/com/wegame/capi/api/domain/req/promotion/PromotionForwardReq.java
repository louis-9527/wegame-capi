package com.wegame.capi.api.domain.req.promotion;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.domain.req.BaseReq;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
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
    private static final String SN_HASH_FIELD_IDS = "snHashFieldIds";
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

    /**
     * 获取明文签名字符串
     * <a href="https://casinoplus.sg.larksuite.com/wiki/SJKbwN1fgiecIlkgg5hlBbRdgqh?renamingWikiNode=false">签名规则</a>
     * @return 明文签名字符串
     */
    public String getPreSignature() {
        if (MapUtils.isEmpty(data)) {
            return buildDefaultPreSignature();
        }

        Object snHashFieldIdsObj = data.get(SN_HASH_FIELD_IDS);
        if (snHashFieldIdsObj == null) {
            return buildDefaultPreSignature();
        }

        // 使用 fastjson 转换为 List<Integer>
        List<Integer> snHashFieldIds = JSON.parseObject(JSON.toJSONString(snHashFieldIdsObj), new TypeReference<List<Integer>>() {});
        if (snHashFieldIds == null || snHashFieldIds.isEmpty()) {
            return buildDefaultPreSignature();
        }

        // 字段列表
        List<String> fields = Lists.newArrayList(
                super.getChannelId(),
                super.getPlayerName(),
                super.getCreateTime(),
                super.getRefId(),
                super.getPId()
        );

        StringBuilder preSignature = new StringBuilder();
        for (Integer index : snHashFieldIds) {
            AssertUtil.assertTrue(index >= 1 && index <= fields.size(), ErrorCode.ERR_PARAM);
            preSignature.append(nullToEmpty(fields.get(index - 1)));
        }
        return preSignature.toString();
    }

    /**
     * 构建默认签名串：channelId + playerName + createTime + refId
     */
    private String buildDefaultPreSignature() {
        return String.format("%s%s%s%s",
                nullToEmpty(super.getChannelId()),
                nullToEmpty(super.getPlayerName()),
                nullToEmpty(super.getCreateTime()),
                nullToEmpty(super.getRefId()));
    }

    /**
     * 安全处理 null 值
     */
    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
