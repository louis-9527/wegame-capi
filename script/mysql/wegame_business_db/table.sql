-- ----------------------------
-- Table structure for gift_amount_record
-- ----------------------------
DROP TABLE IF EXISTS `gift_amount_record`;
CREATE TABLE `gift_amount_record`
(
    `id`              bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`         bigint       NOT NULL COMMENT '用户ID',
    `player_id`       varchar(128) NOT NULL COMMENT '玩家ID',
    `gift_id`         varchar(128) NOT NULL COMMENT '赠送Id(对应营销请求ID)',
    `serial_no`       varchar(128) NOT NULL COMMENT '流水号',
    `change_amount`   bigint       NOT NULL COMMENT '变更金额',
    `change_type`     tinyint      NOT NULL COMMENT '0,增加,1:减少',
    `game_recored_id` varchar(128) NOT NULL COMMENT '游戏记录ID',
    `creator`         varchar(100) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`         varchar(100) NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY (`player_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_unicode_ci COMMENT ='赠送记录';
DROP TABLE IF EXISTS `gift_amount_record`;


-- ----------------------------
-- Table structure for game_record
-- ----------------------------
DROP TABLE IF EXISTS `game_record`;
CREATE TABLE `game_record`
(
    `id`               bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `p_id`             varchar(64)  NOT NULL COMMENT '平台id',
    `channel_id`       varchar(64)  NOT NULL COMMENT '渠道id',
    `game_id`          varchar(64)  NOT NULL COMMENT '游戏id',
    `user_id`          bigint       NOT NULL COMMENT '用户id',
    `player_id`        varchar(64)  NOT NULL COMMENT '玩家id',
    `player_name`      varchar(64)  NOT NULL COMMENT '玩家名称',
    `bet_type`         tinyint      NOT NULL COMMENT '投注类型 1正常投注 2免费轮投注',
    `bet_sub_type`     tinyint      NOT NULL COMMENT '投注子类型 11正常投注 21购买免费轮投注 22非购买免费轮投注',
    `bet_id`           varchar(64)  NOT NULL DEFAULT '' COMMENT '投注id（单钱包）',
    `bet_serial_no`    varchar(64)  NOT NULL COMMENT '投注流水号（对应单钱包refId）',
    `bet_amount`       bigint       NOT NULL DEFAULT 0 COMMENT '投注金额 单位：分',
    `pay_out_amount`   bigint       NOT NULL DEFAULT 0 COMMENT '派彩金额 单位：分',
    `market_flag`      tinyint               DEFAULT 0 COMMENT '营销标识 0否 1是',
    `status`           tinyint      NOT NULL COMMENT '状态 0进行中 1已完结 2无效',
    `free_spins_flag`  tinyint               DEFAULT 0 COMMENT '免费轮获得标识 0否 1是',
    `free_spins_times` tinyint               DEFAULT 0 COMMENT '免费轮次数',
    `detail`           text COMMENT '明细json',
    `creator`          varchar(100) NOT NULL DEFAULT '' COMMENT '创建人',
    `create_time`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`          varchar(100) NOT NULL DEFAULT '' COMMENT '更新人',
    `update_time`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_unicode_ci COMMENT ='游戏记录';

-- ----------------------------
-- Table structure for game_record_detail（不用游戏明细模型不同）
-- ----------------------------
DROP TABLE IF EXISTS `game_record_detail`;
CREATE TABLE `game_record_detail`
(
    `id`             bigint         NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `bet_amount`     bigint         NOT NULL COMMENT '投注金额 单位：分',
    `pay_out_amount` bigint         NOT NULL DEFAULT 0 COMMENT '派彩金额 单位：分',
    `game_result`    text           NOT NULL COMMENT '游戏结果json',
    `win_result`     text           NOT NULL COMMENT '中奖集合json',
    `odds`           decimal(10, 2) NOT NULL COMMENT '赔率',
    `multiple`       int                     DEFAULT null COMMENT '倍数',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE utf8mb4_unicode_ci COMMENT ='游戏记录明细';
