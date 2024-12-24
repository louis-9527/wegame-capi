use
    `wegame_user_db`;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`                bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `player_id`         varchar(128) NOT NULL COMMENT '玩家id',
    `name`              varchar(50)  NOT NULL DEFAULT '' COMMENT '玩家姓名',
    `nick_name`         varchar(50)  NOT NULL DEFAULT '' COMMENT '玩家昵称',
    `real_name`         varchar(50)  NOT NULL DEFAULT '' COMMENT '玩家真实姓名',
    `phone_number`      varchar(20)  NOT NULL DEFAULT '' COMMENT '电话号码',
    `gender`            tinyint      NOT NULL DEFAULT '-1' COMMENT '性别: -1未知',
    `player_avatar`     varchar(255) NOT NULL DEFAULT '' COMMENT '玩家头像',
    `password`          varchar(255) NOT NULL DEFAULT '' COMMENT '密码',
    `email`             varchar(100) NOT NULL DEFAULT '' COMMENT '邮箱',
    `nationality`       varchar(128) NOT NULL DEFAULT '' COMMENT '国家',
    `province`          varchar(255) NOT NULL DEFAULT '' COMMENT '省',
    `city`              varchar(255) NOT NULL DEFAULT '' COMMENT '市',
    `address`           varchar(255) NOT NULL DEFAULT '' COMMENT '地址',
    `platform_id`       varchar(64)  NOT NULL COMMENT '平台id',
    `registration_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `status`            tinyint      NOT NULL DEFAULT '1' COMMENT '用户状态：1为正常，其他非正常',
    `extend`            text COMMENT '扩展信息',
    `creator`           varchar(100) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`           varchar(100) NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`player_id`, `platform_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_unicode_ci COMMENT ='玩家用户';
