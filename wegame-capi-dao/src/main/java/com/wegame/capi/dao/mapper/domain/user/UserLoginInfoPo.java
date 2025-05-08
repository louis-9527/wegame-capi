package com.wegame.capi.dao.mapper.domain.user;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * user_login_info：用户登录信息
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserLoginInfoPo implements Serializable {
    private static final long serialVersionUID = -125366309433197568L;
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 渠道id
     */
    private Long channelId;
    /**
     * 游戏id
     */
    private String gameId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 玩家id
     */
    private String playerId;
    /**
     * 注册时间
     */
    private Date registerTime;
    /**
     * 登录时间
     */
    private Date loginTime;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新人
     */
    private String updater;
    /**
     * 更新时间
     */
    private Date updateTime;

   ///////////////  above auto generated ///////////////
}

