-- ----------------------------
-- Table structure for user
-- ----------------------------
CREATE TABLE `user`
(
    `id`             bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `platform_id`    varchar(64)  NOT NULL COMMENT '平台id',
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
    UNIQUE KEY (`player_id`, `platform_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_unicode_ci COMMENT ='用户';

-- ----------------------------
-- Table structure for user_present_amount_record
-- ----------------------------
CREATE TABLE `user_present_amount_record`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     bigint       NOT NULL COMMENT '用户ID',
    `player_id`   varchar(128) NOT NULL COMMENT '玩家ID',
    `type`        tinyint      NOT NULL COMMENT '类型：1增加,2扣减',
    `sub_type`    tinyint      NOT NULL COMMENT '子类型：11营销赠送, 12游戏赠送失败回滚, 21游戏赠送后扣减',
    `biz_Id`      varchar(128) NOT NULL COMMENT '业务Id(增加：赠送流水号Id/,减少为投注记录Id)',
    `amount`      bigint       NOT NULL COMMENT '变更金额',
    `status`      tinyint      NOT NULL COMMENT '状态：0进行中,1已完成',
    `creator`     varchar(100) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(100) NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY (`user_id`),
    KEY (`player_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_unicode_ci COMMENT ='用户赠送金额记录';