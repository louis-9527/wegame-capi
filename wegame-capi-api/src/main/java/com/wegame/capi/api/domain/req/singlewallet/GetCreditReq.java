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


}
