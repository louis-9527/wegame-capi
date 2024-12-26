use
    `wegame_business_db`;

-- ----------------------------
-- Table structure for bet_record
-- ----------------------------
CREATE TABLE `bet_record`
(
    `id`             bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`        bigint       NOT NULL COMMENT '用户id',
    `player_id`      varchar(64)  NOT NULL COMMENT '玩家id',
    `player_name`    varchar(64)  NOT NULL COMMENT '玩家名称',
    `game_id`        varchar(64)  NOT NULL COMMENT '游戏id',
    `type`           tinyint      NOT NULL COMMENT '类型 1正常投注 2购买免费轮 3获得免费轮',
    `bet_id`         varchar(64)  NOT NULL DEFAULT '' COMMENT '投注id（单钱包）',
    `serial_no`      varchar(64)  NOT NULL COMMENT '流水号（幂等key）',
    `status`         tinyint      NOT NULL COMMENT '状态 0进行中 1已完结 2无效',
    `bet_amount`     bigint       NOT NULL DEFAULT 0 COMMENT '投注金额.单位：分',
    `bet_time`       timestamp COMMENT '投注时间',
    `pay_out_amount` bigint       NOT NULL DEFAULT 0 COMMENT '派彩金额.单位：分',
    `free_spins`     tinyint               DEFAULT 0 COMMENT '是否触发免费轮 0否 1是',
    `spins_record`   text COMMENT '游戏轮记录',
    `creator`        varchar(100) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        varchar(100) NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_unicode_ci COMMENT ='投注记录';
DROP TABLE IF EXISTS `user`;

-- ----------------------------
-- Table structure for spins_record
-- ----------------------------
CREATE TABLE `spins_record`
(
    `id`             bigint         NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `bet_amount`     bigint         NOT NULL COMMENT '投注金额.单位：分',
    `pay_out_amount` bigint         NOT NULL DEFAULT 0 COMMENT '派彩金额.单位：分',
    `spins_result`   text           NOT NULL DEFAULT '' COMMENT '游戏论结果',
    `odds`           decimal(10, 2) NOT NULL DEFAULT '' COMMENT '赔率',
    `multiple`       int                     default null COMMENT '倍数',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_unicode_ci COMMENT ='游戏轮记录';
DROP TABLE IF EXISTS `user`;
