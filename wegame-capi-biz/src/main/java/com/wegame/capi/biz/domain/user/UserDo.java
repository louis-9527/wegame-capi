package com.wegame.capi.biz.domain.user;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * user：玩家用户
 * @author generate
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserDo implements Serializable {
    private static final long serialVersionUID = -2266308277093710848L;
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 玩家id
     */
    private String playerId;
    /**
     * 玩家姓名
     */
    private String name;
    /**
     * 玩家昵称
     */
    private String nickName;
    /**
     * 玩家真实姓名
     */
    private String realName;
    /**
     * 电话号码
     */
    private String phoneNumber;
    /**
     * 性别: -1未知
     */
    private Integer gender;
    /**
     * 玩家头像
     */
    private String playerAvatar;
    /**
     * 密码
     */
    private String password;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 国家
     */
    private String nationality;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 地址
     */
    private String address;
    /**
     * 平台id
     */
    private String platformId;
    /**
     * 注册时间
     */
    private Date registrationTime;
    /**
     * 用户状态：1为正常，其他非正常
     */
    private Integer status;
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

