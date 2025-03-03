package com.wegame.capi.api.domain.req.singlewallet;

import com.wegame.capi.api.domain.req.BaseReq;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@ToString
@ApiModel("获取用户余额请求模型")
public class GetCreditReq extends BaseReq {


}
