package com.wegame.capi.dao.mapper.domain.user;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * user：用户
 * @author genereate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserPo implements Serializable {
    private static final long serialVersionUID = -1304646857779625984L;
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 平台id
     */
    private String platformId;
    /**
     * 用户姓名
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
     * 赠送金额
     */
    private Long presentAmount;
    /**
     * 扩展信息
     */
    private String extend;
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

