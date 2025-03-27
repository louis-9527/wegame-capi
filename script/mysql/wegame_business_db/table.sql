use
    `wegame_business_db`;

-- ----------------------------
-- Table structure for bet_record
-- 存储 mongodb
-- ----------------------------

CREATE TABLE `bet_record`
(
    `id`             bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `p_id`           varchar(64)  NOT NULL COMMENT '平台id',
    `channel_id`     varchar(64)  NOT NULL COMMENT '渠道id',
    `game_id`        varchar(64)  NOT NULL COMMENT '游戏id',
    `user_id`        bigint       NOT NULL COMMENT '用户id',
    `player_id`      varchar(64)  NOT NULL COMMENT '玩家id',
    `player_name`    varchar(64)  NOT NULL COMMENT '玩家名称',
    `bet_type`       tinyint      NOT NULL COMMENT '投注类型 NORMAL_BET（正常投注）; BUY_BET（购买投注）',
    `bet_sub_type`   tinyint      NOT NULL COMMENT '投注子类型 NORMAL_BET（正常投注）; FREE_SPINS_NORMAL_BET（免费轮正常投注）; BUY_BET（购买投注）; FREE_SPINS_BUY_BET（免费轮购买投注）',
    `bet_id`         varchar(64)  NOT NULL DEFAULT '' COMMENT '投注id（单钱包）',
    `bet_serial_no`  varchar(64)  NOT NULL COMMENT '投注流水号（对应单钱包refId）',
    `bet_amount`     bigint       NOT NULL DEFAULT 0 COMMENT '投注金额 单位：分',
    `pay_amount`     bigint       NOT NULL DEFAULT 0 COMMENT '支付金额 单位：分',
    `pay_out_amount` bigint       NOT NULL DEFAULT 0 COMMENT '派彩总金额 单位：分',
    `market_flag`    tinyint               DEFAULT 0 COMMENT '营销标识 0否 1是',
    `status`         tinyint      NOT NULL COMMENT '状态 0进行中 1已完结 2无效',
    `detail`         text COMMENT '明细',
    `creator`        varchar(100) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        varchar(100) NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_unicode_ci COMMENT ='投注记录';