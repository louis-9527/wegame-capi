package com.wegame.capi.client.context;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Map;

/**
 * 玩家
 *
 * @author apple
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("用户信息")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo implements Serializable {
    private static final long serialVersionUID = -701740428094376475L;

    @ApiModelProperty("id")
    private String id;
    @JsonProperty("pId")
    @ApiModelProperty(value = "平台id")
    private String pId;
    @ApiModelProperty(value = "渠道id")
    private String channelId;
    @ApiModelProperty(value = "游戏id")
    private String gameId;
    @ApiModelProperty(value = "用户id")
    private String playerId;
    @ApiModelProperty(value = "用户姓名")
    private String playerName;
    @ApiModelProperty(value = "用户昵称")
    private String nickName;
    @ApiModelProperty(value = "用户级别")
    private String userLevel;
    @ApiModelProperty(value = "用户姓名")
    private String userName;
    @ApiModelProperty(value = "中台token")
    private String token;
    @ApiModelProperty(value = "AB测试(afe: A（数值化）, aff: B（模拟盘面/模版）)")
    private String ut = "aff";
    @ApiModelProperty(value = "扩展参数")
    private Map<String, Object> ext;
}