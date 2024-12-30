package com.wegame.capi.api.domain.req.singlewallet;

import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.common.context.UserInfoContext;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.slf4j.MDC;

import java.util.Date;

@Data
@SuperBuilder
@ToString
@ApiModel("获取用户余额请求模型")
public class GetCreditReq extends SingleWalletBaseReq {

    public static GetCreditReq build() {
        return GetCreditReq.builder()
                .channelId(UserInfoContext.getUserInfo().getChannelId())
                .playerName(UserInfoContext.getUserInfo().getPlayerName())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .createTime(DateUtil.toUTC(new Date()))
                .pId(UserInfoContext.getUserInfo().getPlatformId())
                .build();
    }
}
