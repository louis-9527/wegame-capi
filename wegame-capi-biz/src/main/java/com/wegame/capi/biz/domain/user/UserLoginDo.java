package com.wegame.capi.biz.domain.user;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 用户登录
 *
 * @author generate
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserLoginDo implements Serializable {
    private static final long serialVersionUID = -1800496652360985600L;
    /**
     * 平台id
     */
    private String pId;
    /**
     * 渠道id
     */
    private String channelId;
    /**
     * 游戏id
     */
    private String gameId;
    /**
     * 玩家id
     */
    private String playerId;
    /**
     * 用户姓名
     */
    private String playerName;
    /**
     * 用户昵称
     */
    private String nickName;
    /**
     * 用户级别
     */
    private String userLevel;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 中台token
     */
    private String token;
    /**
     * AB测试(afe: A, aff: B)
     * @see com.wegame.capi.client.enums.AbTypeEnum
     */
    private String ut;
}

