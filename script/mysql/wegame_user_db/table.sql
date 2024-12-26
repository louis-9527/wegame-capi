use
    `wegame_user_db`;

-- ----------------------------
-- Table structure for user
-- ----------------------------
CREATE TABLE `user`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `platform_id` varchar(64)  NOT NULL COMMENT '平台id',
    `channel_id`  varchar(64)  NOT NULL COMMENT '渠道id',
    `game_id`     varchar(128) NOT NULL COMMENT '游戏id',
    `player_name` varchar(128) NOT NULL COMMENT '玩家姓名',
    `nick_name`   varchar(50)  NOT NULL DEFAULT '' COMMENT '玩家昵称',
    `extend`      text COMMENT '扩展信息',
    `creator`     varchar(100) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(100) NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`player_name`, `platform_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_unicode_ci COMMENT ='玩家用户';
DROP TABLE IF EXISTS `user`;
