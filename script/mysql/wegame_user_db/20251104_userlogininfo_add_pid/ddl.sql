-- 添加 p_id 列
ALTER TABLE `wegame_user_db`.`user_login_info`
    ADD COLUMN `p_id` varchar(64) NOT NULL DEFAULT '50' COMMENT '平台id' AFTER `id`;
-- 将线上channel_id = 3051 的数据 p_id 改为 55
update wegame_user_db.user_login_info set p_id = '55' where channel_id = '3051';

-- 删除原有的唯一索引
ALTER TABLE `wegame_user_db`.`user_login_info`
DROP INDEX `channel_id`;

-- 添加新的唯一索引
ALTER TABLE `wegame_user_db`.`user_login_info`
    ADD UNIQUE KEY (`p_id`, `channel_id`, `game_id`, `user_id`, `player_id`);
