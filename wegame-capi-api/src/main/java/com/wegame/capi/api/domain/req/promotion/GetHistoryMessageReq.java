package com.wegame.capi.api.domain.req.promotion;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GetHistoryMessageReq extends BaseReq {
    private static final long serialVersionUID = -2819042898670268022L;
    /**
     * 页码
     */
    private Integer page;
    /**
     * 条数
     */
    private Integer limit;

}
