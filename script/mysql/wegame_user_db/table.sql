use
`wegame_user_db`;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`             bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `p_id`           varchar(64)  NOT NULL COMMENT '平台id',
    `player_id`      varchar(128) NOT NULL COMMENT '用户姓名',
    `player_name`    varchar(128) NOT NULL COMMENT '用户姓名',
    `nick_name`      varchar(50)  NOT NULL DEFAULT '' COMMENT '用户昵称',
    `user_level`     varchar(50)  NOT NULL DEFAULT '' COMMENT '用户级别',
    `user_name`      varchar(50)  NOT NULL DEFAULT '' COMMENT '用户姓名',
    `present_amount` bigint       NOT NULL DEFAULT 0 COMMENT '赠送金额',
    `extend`         text COMMENT '扩展信息',
    `creator`        varchar(100) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        varchar(100) NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`player_id`, `p_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_unicode_ci COMMENT ='用户';

-- ----------------------------
-- Table structure for user_login_info
-- ----------------------------
DROP TABLE IF EXISTS `user_login_info`;
CREATE TABLE `user_login_info`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `p_id`          varchar(64)  NOT NULL DEFAULT '50' COMMENT '平台id',
    `channel_id`    bigint       NOT NULL COMMENT '渠道id',
    `game_id`       varchar(20)  NOT NULL COMMENT '游戏id',
    `user_id`       bigint       NOT NULL COMMENT '用户id',
    `player_id`     varchar(128) NOT NULL COMMENT '玩家id',
    `register_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `login_time`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    `creator`       varchar(100) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`       varchar(100) NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`p_id`,`channel_id`, `game_id`, `user_id`, `player_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_unicode_ci COMMENT ='用户登录信息';