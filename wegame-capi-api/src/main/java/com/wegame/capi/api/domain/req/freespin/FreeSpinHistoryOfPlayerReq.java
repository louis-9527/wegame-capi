package com.wegame.capi.api.domain.req.freespin;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FreeSpinHistoryOfPlayerReq extends BaseReq {
    private static final long serialVersionUID = 349333654115861622L;
    /** Query page starting from 1 */
    private Integer page;
    /** Max limit of data return not more than 100 */
    private Integer limit;
    /** Query start time based on player acquire free spin time [Unix timestamps in seconds]  */
    private Integer startTime;
    /** Query end time based on player acquire free spin time [Unix timestamps in seconds]  */
    private Integer endTime;
}

