package com.wegame.capi.dao.mapper.business;

import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * jackpot_user_reward_record：用户jackpot中奖记录
 * 方法顺序：增删改查
 * @author generate
 */
@DS("wegame_business_db")
@Mapper
public interface JackpotUserRewardRecordMapper {
    /** 增 */
    @Insert({"insert into `jackpot_user_reward_record`(" +
            " `user_id`, " +
            " `player_name`, " +
            " `bet_record_id`, " +
            " `game_record_id`, " +
            " `bet_id`, " +
            " `reward_pool_id`, " +
            " `reward_pool_type`, " +
            " `reward_amount`, " +
            " `reward_grant_serial_no`, " +
            " `reward_grant_status`, " +
            " `reward_grant_time`, " +
            " `creator`, " +
            " `updater`, " +
            " `create_time`, " +
            " `update_time` " +
            ") values (" +
            " #{userId}, " +
            " #{playerName}, " +
            " #{betRecordId}, " +
            " #{gameRecordId}, " +
            " #{betId}, " +
            " #{rewardPoolId}, " +
            " #{rewardPoolType}, " +
            " #{rewardAmount}, " +
            " #{rewardGrantSerialNo}, " +
            " #{rewardGrantStatus}, " +
            " #{rewardGrantTime}, " +
            " #{creator}, " +
            " #{updater}, " +
            " current_timestamp(), " +
            " current_timestamp()" +
            ")"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(JackpotUserRewardRecordPo jackpotUserRewardRecord);

    @Insert({"<script>",
        " insert into `jackpot_user_reward_record` (",
        "   `user_id`, ",
        "   `player_name`, ",
        "   `bet_record_id`, ",
        "   `game_record_id`, ",
        "   `bet_id`, ",
        "   `reward_pool_id`, ",
        "   `reward_pool_type`, ",
        "   `reward_amount`, ",
        "   `reward_grant_serial_no`, ",
        "   `reward_grant_status`, ",
        "   `reward_grant_time`, ",
        "   `creator`, ",
        "   `updater`, ",
        "   `create_time`, ",
        "   `update_time` ",
        ") values ",
        " <foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\" nullable=\"true\"> ",
        " (",
        "   #{item.userId}, ",
        "   #{item.playerName}, ",
        "   #{item.betRecordId}, ",
        "   #{item.gameRecordId}, ",
        "   #{item.betId}, ",
        "   #{item.rewardPoolId}, ",
        "   #{item.rewardPoolType}, ",
        "   #{item.rewardAmount}, ",
        "   #{item.rewardGrantSerialNo}, ",
        "   #{item.rewardGrantStatus}, ",
        "   #{item.rewardGrantTime}, ",
        "   #{item.creator}, ",
        "   #{item.updater}, ",
        "   current_timestamp(), ",
        "   current_timestamp()",
        " )",
        " </foreach> ",
        "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int batchInsert(@Param("list") List<JackpotUserRewardRecordPo> list);

    /** 删 */
    @Delete("delete from `jackpot_user_reward_record` where `id` = #{id} limit 1")
    int deleteById(@Param("id") Long id);

    @Delete({"<script>",
        " delete from `jackpot_user_reward_record` where `id` in ",
        " <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
        "   #{item} ",
        " </foreach> ",
        "</script>"})
    int deleteByIds(@Param("ids") List<Long> ids);

    /** 改 */
    @Update({"<script>",
        " update `jackpot_user_reward_record` ",
        " <trim prefix=\"set\" suffixOverrides=\",\"> ",
        "     <if test=\"userId != null\">`user_id` = #{userId},</if> ",
        "     <if test=\"playerName != null\">`player_name` = #{playerName},</if> ",
        "     <if test=\"betRecordId != null\">`bet_record_id` = #{betRecordId},</if> ",
        "     <if test=\"gameRecordId != null\">`game_record_id` = #{gameRecordId},</if> ",
        "     <if test=\"betId != null\">`bet_id` = #{betId},</if> ",
        "     <if test=\"rewardPoolId != null\">`reward_pool_id` = #{rewardPoolId},</if> ",
        "     <if test=\"rewardPoolType != null\">`reward_pool_type` = #{rewardPoolType},</if> ",
        "     <if test=\"rewardAmount != null\">`reward_amount` = #{rewardAmount},</if> ",
        "     <if test=\"rewardGrantSerialNo != null\">`reward_grant_serial_no` = #{rewardGrantSerialNo},</if> ",
        "     <if test=\"rewardGrantStatus != null\">`reward_grant_status` = #{rewardGrantStatus},</if> ",
        "     <if test=\"rewardGrantTime != null\">`reward_grant_time` = #{rewardGrantTime},</if> ",
        "     <if test=\"creator != null\">`creator` = #{creator},</if> ",
        "     <if test=\"updater != null\">`updater` = #{updater},</if> ",
        "     `update_time` = current_timestamp() ",
        " </trim> ",
        " where id = #{id} limit 1",
        "</script>"})
    int updateById(JackpotUserRewardRecordPo jackpotUserRewardRecord);

    /** 查 */
    @Select("select * from `jackpot_user_reward_record` where `id` = #{id} limit 1")
    JackpotUserRewardRecordPo selectById(@Param("id") Long id);

    @Select({"<script>",
            " select * from `jackpot_user_reward_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerName != null and query.playerName != ''\">and player_name like CONCAT('%', #{query.playerName}, '%')</if> ",
            "     <if test=\"query.betRecordId != null\">and `bet_record_id` = #{query.betRecordId}</if> ",
            "     <if test=\"query.gameRecordId != null\">and `game_record_id` = #{query.gameRecordId}</if> ",
            "     <if test=\"query.betId != null\">and `bet_id` = #{query.betId}</if> ",
            "     <if test=\"query.rewardPoolId != null\">and `reward_pool_id` = #{query.rewardPoolId}</if> ",
            "     <if test=\"query.rewardPoolType != null\">and `reward_pool_type` = #{query.rewardPoolType}</if> ",
            "     <if test=\"query.rewardAmount != null\">and `reward_amount` = #{query.rewardAmount}</if> ",
            "     <if test=\"query.rewardGrantSerialNo != null and query.rewardGrantSerialNo != ''\">and reward_grant_serial_no like CONCAT('%', #{query.rewardGrantSerialNo}, '%')</if> ",
            "     <if test=\"query.rewardGrantStatus != null\">and `reward_grant_status` = #{query.rewardGrantStatus}</if> ",
            "     <if test=\"query.rewardGrantTime != null\">and `reward_grant_time` = #{query.rewardGrantTime}</if> ",
            "     <if test=\"query.creator != null and query.creator != ''\">and creator like CONCAT('%', #{query.creator}, '%')</if> ",
            "     <if test=\"query.updater != null and query.updater != ''\">and updater like CONCAT('%', #{query.updater}, '%')</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "     <if test=\"query.orderByClause != null and query.orderByClause != ''\"> ${query.orderByClause} </if> ",
            "     limit 1 ",
            "</script>"})
    JackpotUserRewardRecordPo selectOne(@Param("query") JackpotUserRewardRecordQuery query);

    @Select({"<script>",
            " select id from `jackpot_user_reward_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerName != null and query.playerName != ''\">and player_name like CONCAT('%', #{query.playerName}, '%')</if> ",
            "     <if test=\"query.betRecordId != null\">and `bet_record_id` = #{query.betRecordId}</if> ",
            "     <if test=\"query.gameRecordId != null\">and `game_record_id` = #{query.gameRecordId}</if> ",
            "     <if test=\"query.betId != null\">and `bet_id` = #{query.betId}</if> ",
            "     <if test=\"query.rewardPoolId != null\">and `reward_pool_id` = #{query.rewardPoolId}</if> ",
            "     <if test=\"query.rewardPoolType != null\">and `reward_pool_type` = #{query.rewardPoolType}</if> ",
            "     <if test=\"query.rewardAmount != null\">and `reward_amount` = #{query.rewardAmount}</if> ",
            "     <if test=\"query.rewardGrantSerialNo != null and query.rewardGrantSerialNo != ''\">and reward_grant_serial_no like CONCAT('%', #{query.rewardGrantSerialNo}, '%')</if> ",
            "     <if test=\"query.rewardGrantStatus != null\">and `reward_grant_status` = #{query.rewardGrantStatus}</if> ",
            "     <if test=\"query.rewardGrantTime != null\">and `reward_grant_time` = #{query.rewardGrantTime}</if> ",
            "     <if test=\"query.creator != null and query.creator != ''\">and creator like CONCAT('%', #{query.creator}, '%')</if> ",
            "     <if test=\"query.updater != null and query.updater != ''\">and updater like CONCAT('%', #{query.updater}, '%')</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "     <if test=\"query.orderByClause != null and query.orderByClause != ''\"> ${query.orderByClause} </if> ",
            "     limit 1 ",
            "</script>"})
    Long selectId(@Param("query") JackpotUserRewardRecordQuery query);

    @Select({"<script>",
            " select * from `jackpot_user_reward_record` where `id` in ",
            "   <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
            "     #{item} ",
            "   </foreach> ",
            "</script>"})
    List<JackpotUserRewardRecordPo> listByIds(@Param("ids") List<Long> ids);

    @Select("select * from `jackpot_user_reward_record` order by `create_time` desc")
    List<JackpotUserRewardRecordPo> listAll();

    @Select({"<script>",
            " select count(1) from `jackpot_user_reward_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerName != null and query.playerName != ''\">and player_name like CONCAT('%', #{query.playerName}, '%')</if> ",
            "     <if test=\"query.betRecordId != null\">and `bet_record_id` = #{query.betRecordId}</if> ",
            "     <if test=\"query.gameRecordId != null\">and `game_record_id` = #{query.gameRecordId}</if> ",
            "     <if test=\"query.betId != null\">and `bet_id` = #{query.betId}</if> ",
            "     <if test=\"query.rewardPoolId != null\">and `reward_pool_id` = #{query.rewardPoolId}</if> ",
            "     <if test=\"query.rewardPoolType != null\">and `reward_pool_type` = #{query.rewardPoolType}</if> ",
            "     <if test=\"query.rewardAmount != null\">and `reward_amount` = #{query.rewardAmount}</if> ",
            "     <if test=\"query.rewardGrantSerialNo != null and query.rewardGrantSerialNo != ''\">and reward_grant_serial_no like CONCAT('%', #{query.rewardGrantSerialNo}, '%')</if> ",
            "     <if test=\"query.rewardGrantStatus != null\">and `reward_grant_status` = #{query.rewardGrantStatus}</if> ",
            "     <if test=\"query.rewardGrantTime != null\">and `reward_grant_time` = #{query.rewardGrantTime}</if> ",
            "     <if test=\"query.creator != null and query.creator != ''\">and creator like CONCAT('%', #{query.creator}, '%')</if> ",
            "     <if test=\"query.updater != null and query.updater != ''\">and updater like CONCAT('%', #{query.updater}, '%')</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "</script>"})
    long count(@Param("query") JackpotUserRewardRecordQuery query);

    @Select({"<script>",
            " select * from `jackpot_user_reward_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerName != null and query.playerName != ''\">and player_name like CONCAT('%', #{query.playerName}, '%')</if> ",
            "     <if test=\"query.betRecordId != null\">and `bet_record_id` = #{query.betRecordId}</if> ",
            "     <if test=\"query.gameRecordId != null\">and `game_record_id` = #{query.gameRecordId}</if> ",
            "     <if test=\"query.betId != null\">and `bet_id` = #{query.betId}</if> ",
            "     <if test=\"query.rewardPoolId != null\">and `reward_pool_id` = #{query.rewardPoolId}</if> ",
            "     <if test=\"query.rewardPoolType != null\">and `reward_pool_type` = #{query.rewardPoolType}</if> ",
            "     <if test=\"query.rewardAmount != null\">and `reward_amount` = #{query.rewardAmount}</if> ",
            "     <if test=\"query.rewardGrantSerialNo != null and query.rewardGrantSerialNo != ''\">and reward_grant_serial_no like CONCAT('%', #{query.rewardGrantSerialNo}, '%')</if> ",
            "     <if test=\"query.rewardGrantStatus != null\">and `reward_grant_status` = #{query.rewardGrantStatus}</if> ",
            "     <if test=\"query.rewardGrantTime != null\">and `reward_grant_time` = #{query.rewardGrantTime}</if> ",
            "     <if test=\"query.creator != null and query.creator != ''\">and creator like CONCAT('%', #{query.creator}, '%')</if> ",
            "     <if test=\"query.updater != null and query.updater != ''\">and updater like CONCAT('%', #{query.updater}, '%')</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "     <if test=\"query.orderByClause != null and query.orderByClause != ''\"> ${query.orderByClause} </if> ",
            "     <if test=\"query.limit != null\"> limit #{query.limit}  </if> ",
            "     <if test=\"query.offset != null\"> offset #{query.offset}  </if> ",
            "</script>"})
    List<JackpotUserRewardRecordPo> list(@Param("query") JackpotUserRewardRecordQuery query);


    @Select({"<script>",
            " select id from `jackpot_user_reward_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerName != null and query.playerName != ''\">and player_name like CONCAT('%', #{query.playerName}, '%')</if> ",
            "     <if test=\"query.betRecordId != null\">and `bet_record_id` = #{query.betRecordId}</if> ",
            "     <if test=\"query.gameRecordId != null\">and `game_record_id` = #{query.gameRecordId}</if> ",
            "     <if test=\"query.betId != null\">and `bet_id` = #{query.betId}</if> ",
            "     <if test=\"query.rewardPoolId != null\">and `reward_pool_id` = #{query.rewardPoolId}</if> ",
            "     <if test=\"query.rewardPoolType != null\">and `reward_pool_type` = #{query.rewardPoolType}</if> ",
            "     <if test=\"query.rewardAmount != null\">and `reward_amount` = #{query.rewardAmount}</if> ",
            "     <if test=\"query.rewardGrantSerialNo != null and query.rewardGrantSerialNo != ''\">and reward_grant_serial_no like CONCAT('%', #{query.rewardGrantSerialNo}, '%')</if> ",
            "     <if test=\"query.rewardGrantStatus != null\">and `reward_grant_status` = #{query.rewardGrantStatus}</if> ",
            "     <if test=\"query.rewardGrantTime != null\">and `reward_grant_time` = #{query.rewardGrantTime}</if> ",
            "     <if test=\"query.creator != null and query.creator != ''\">and creator like CONCAT('%', #{query.creator}, '%')</if> ",
            "     <if test=\"query.updater != null and query.updater != ''\">and updater like CONCAT('%', #{query.updater}, '%')</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "     <if test=\"query.orderByClause != null and query.orderByClause != ''\"> ${query.orderByClause} </if> ",
            "     <if test=\"query.limit != null\"> limit #{query.limit}  </if> ",
            "     <if test=\"query.offset != null\"> offset #{query.offset}  </if> ",
            "</script>"})
    List<Long> listId(@Param("query") JackpotUserRewardRecordQuery query);
}

