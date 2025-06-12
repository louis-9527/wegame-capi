

// 切换到目标数据库（若不存在会自动创建）
use lamang;

// 批量插入数据
db.lamang_jackpot_reward_pool.insertMany([
    { _id: 1,type:1, amount: 0, creator: "system", createTime: 1747903796,updater: "system", updateTime: 1747903796},
    { _id: 2,type:2, amount: 0, creator: "system", createTime: 1747903796,updater: "system", updateTime: 1747903796},
    { _id: 3,type:3, amount: 0, creator: "system", createTime: 1747903796,updater: "system", updateTime: 1747903796},
    { _id: 4,type:4, amount: 0, creator: "system", createTime: 1747903796,updater: "system", updateTime: 1747903796},
]);
// jackpot中奖记录唯一键
db.lamang_jackpot_reward_pool.createIndex(
    { type: 1},
    { unique: true}
);

// jackpot中奖记录唯一键
db.lamang_jackpot_user_reward_record.createIndex(
    { gameRecordId: 1,betRecordId: 1},
    { unique: true}
);

// 奖池流水ttl，索引:在expireAtTime时间的时候删除
db.getCollection("lamang_jackpot_reward_pool_input_record").createIndex(
    { "expireAtTime": 1 },
    { expireAfterSeconds: 0 }
);

db.getCollection("lamang_bet_record").drop();
db.createCollection("lamang_bet_record");
// 索引lamang_bet_record-c端查询需要
db.lamang_bet_record.createIndex({playerId: 1, channelId: 1, status: 1, createTime: -1});
db.lamang_bet_record.createIndex({createTime: -1, status: 1, _id: 1});
// 索引lamang_bet_record-b端查询需要-使用覆盖索引
db.lamang_bet_record.createIndex({
    createTime: -1,
    channelId: 1,
    status: 1,
    payAmount: 1,
    payOutAmount: 1,
    userId: 1,
    betPromotionType: 1
});


// ----------------------------
//  Collection structure for lamang_game_record
// ----------------------------
db.getCollection("lamang_game_record").drop();
db.createCollection("lamang_game_record");
// 索引lamang_game_record-c端查询需要-新
db.lamang_game_record.createIndex({betRecordId: 1, batchId: 1});

// -- ----------------------------
//     -- Table structure for xxx_jackpot_pool: xxx_jackpot奖池
// -- ----------------------------
//     DROP TABLE IF EXISTS `xxx_jackpot_reward_pool`;
// create table `xxx_jackpot_reward_pool`
// (
//     `id`          bigint      NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
//     `type`        tinyint     NOT NULL COMMENT '类型 1：grand 2:major 3:minor 4:mini',
//     `amount`      bigint      NOT NULL DEFAULT 0 COMMENT '奖池金额.单位：厘， 可以为负',
//     `creator`     varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
//     `create_time` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//     `updater`     varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
//     `update_time` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
//     UNIQUE KEY `idx_type` (`type`)
// ) ENGINE = InnoDB
// DEFAULT CHARSET = utf8mb4
// COLLATE utf8mb4_unicode_ci COMMENT ='xxxx奖池';
//
// -- ----------------------------
//     -- Table structure for xxx_jackpot_reward_pool_input_record: jackpot奖池流入记录
// --     初始化流入：业务id为0
// --     派彩后流入：业务id为 betRocordId
// -- ----------------------------
//     DROP TABLE IF EXISTS `xxx_jackpot_reward_pool_input_record`;
// create table `xxx_jackpot_reward_pool_input_record`
// (
//     `id`             bigint      NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID/投注记录ID',
//     `type`           tinyint     NOT NULL COMMENT '类型.10派彩后流入',
//     `grand_amount`   bigint      NOT NULL COMMENT '金额: 单位分',
//     `major_amount`   bigint      NOT NULL COMMENT '金额: 单位分',
//     `minor_amount`   bigint      NOT NULL COMMENT '金额: 单位分',
//     `mini_amount`    bigint      NOT NULL COMMENT '金额: 单位分',
//     `creator`        varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
//     `create_time`    timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
//     `updater`        varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
//     `update_time`    timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
// ) ENGINE = InnoDB
// DEFAULT CHARSET = utf8mb4
// COLLATE utf8mb4_unicode_ci COMMENT ='xxx_jackpot奖池流入记录';
//
// -- ----------------------------
//     -- Table structure for xxx_user_jackpot_reward_record：xxx_用户jackpot中奖记录
// -- ----------------------------
//     DROP TABLE IF EXISTS `xxx_user_jackpot_reward_record`;
// create table `xxx_user_jackpot_reward_record`
// (
//     `id`                         bigint      NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
//     `user_id`                    bigint      NOT NULL COMMENT '用户id',
//     `player_name`                varchar(64)      NOT NULL COMMENT '玩家id',
//     `bet_record_id`              timestamp  NOT NULL  COMMENT '投注id',
//     `game_record_id`             timestamp  NOT NULL  COMMENT '投注id',
//     `bet_id`                     bigint  NOT NULL  COMMENT '投注id（单钱包）',
//     `reward_pool_id`             bigint      NOT NULL COMMENT '奖池id',
//     `reward_pool_type`           tinyint     NOT NULL COMMENT '类型 1：grand 2:major 3:minor 4:mini 5:betBonus',
//     `reward_amount`              bigint      NOT NULL DEFAULT 0 COMMENT '中奖金额.单位：厘',
//     `reward_grant_serial_no`     varchar(64) NOT NULL COMMENT '派奖流水号（refid）',
//     `reward_grant_status`        tinyint     NOT NULL DEFAULT 0 COMMENT '派奖状态.0:未派奖.1:派奖中（状态待定）.2:已派奖',
//     `reward_grant_time`          timestamp COMMENT '派奖成功时间',
//     `creator`                    varchar(50) NOT NULL DEFAULT '' COMMENT '创建人',
//     `create_time`                timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间/中奖时间',
//     `updater`                    varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',
//     `update_time`                timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
//     KEY                          `idx_createTime` (`create_time`),
//     UNIQUE KEY `idx` (`bet_record_id`, `reward_pool_id`)，
// ) ENGINE = InnoDB
// DEFAULT CHARSET = utf8mb4
// COLLATE utf8mb4_unicode_ci COMMENT ='用户jackpot中奖记录';