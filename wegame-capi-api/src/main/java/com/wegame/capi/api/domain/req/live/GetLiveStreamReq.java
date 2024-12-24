package com.wegame.capi.api.domain.req.live;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 获取直播信息req
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class GetLiveStreamReq extends BaseReq {
    private static final long serialVersionUID = -5876931849825579455L;

    /**
     * 1:NBA Livestream，2:Boxing，默认：0 (全部)
     */
    private Integer type;

    /**
     * 1:列表，2:轮播，默认：1
     */
    private Integer section;

    /**
     * boxing 赛事 Id
     */
    private String boxingEventId;

    /**
     * 赛事 ID
     */
    private String imMatchGroupId;

}
