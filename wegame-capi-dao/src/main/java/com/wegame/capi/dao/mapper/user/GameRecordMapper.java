package com.wegame.capi.dao.mapper.user;

import com.wegame.capi.dao.mapper.domain.user.BetRecordPo;
import com.wegame.capi.dao.mapper.domain.user.BetRecordQuery;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * game_record：投注记录
 * 方法顺序：增删改查
 * @author generate
 */
@DS("wegame_business_db")
@Mapper
public interface GameRecordMapper {
    /** 增 */
    @Insert({"insert into `game_record`(" +
            " `p_id`, " +
            " `channel_id`, " +
            " `game_id`, " +
            " `user_id`, " +
            " `player_id`, " +
            " `player_name`, " +
            " `bet_type`, " +
            " `bet_sub_type`, " +
            " `bet_id`, " +
            " `bet_serial_no`, " +
            " `bet_amount`, " +
            " `pay_out_amount`, " +
            " `market_flag`, " +
            " `status`, " +
            " `free_spins_flag`, " +
            " `free_spins_times`, " +
            " `detail`, " +
            " `creator`, " +
            " `updater`, " +
            " `create_time`, " +
            " `update_time` " +
            ") values (" +
            " #{pId}, " +
            " #{channelId}, " +
            " #{gameId}, " +
            " #{userId}, " +
            " #{playerId}, " +
            " #{playerName}, " +
            " #{betType}, " +
            " #{betSubType}, " +
            " #{betId}, " +
            " #{betSerialNo}, " +
            " #{betAmount}, " +
            " #{payOutAmount}, " +
            " #{marketFlag}, " +
            " #{status}, " +
            " #{freeSpinsFlag}, " +
            " #{freeSpinsTimes}, " +
            " #{detail}, " +
            " #{creator}, " +
            " #{updater}, " +
            " current_timestamp(), " +
            " current_timestamp()" +
            ")"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(BetRecordPo gameRecord);

    @Insert({"<script>",
        " insert into `game_record` (",
        "   `p_id`, ",
        "   `channel_id`, ",
        "   `game_id`, ",
        "   `user_id`, ",
        "   `player_id`, ",
        "   `player_name`, ",
        "   `bet_type`, ",
        "   `bet_sub_type`, ",
        "   `bet_id`, ",
        "   `bet_serial_no`, ",
        "   `bet_amount`, ",
        "   `pay_out_amount`, ",
        "   `market_flag`, ",
        "   `status`, ",
        "   `free_spins_flag`, ",
        "   `free_spins_times`, ",
        "   `detail`, ",
        "   `creator`, ",
        "   `updater`, ",
        "   `create_time`, ",
        "   `update_time` ",
        ") values ",
        " <foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\" nullable=\"true\"> ",
        " (",
        "   #{item.pId}, ",
        "   #{item.channelId}, ",
        "   #{item.gameId}, ",
        "   #{item.userId}, ",
        "   #{item.playerId}, ",
        "   #{item.playerName}, ",
        "   #{item.betType}, ",
        "   #{item.betSubType}, ",
        "   #{item.betId}, ",
        "   #{item.betSerialNo}, ",
        "   #{item.betAmount}, ",
        "   #{item.payOutAmount}, ",
        "   #{item.marketFlag}, ",
        "   #{item.status}, ",
        "   #{item.freeSpinsFlag}, ",
        "   #{item.freeSpinsTimes}, ",
        "   #{item.detail}, ",
        "   #{item.creator}, ",
        "   #{item.updater}, ",
        "   current_timestamp(), ",
        "   current_timestamp()",
        " )",
        " </foreach> ",
        "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int batchInsert(@Param("list") List<BetRecordPo> list);

    /** 删 */
    @Delete("delete from `game_record` where `id` = #{id} limit 1")
    int deleteById(@Param("id") Long id);

    @Delete({"<script>",
        " delete from `game_record` where `id` in ",
        " <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
        "   #{item} ",
        " </foreach> ",
        "</script>"})
    int deleteByIds(@Param("ids") List<Long> ids);

    /** 改 */
    @Update({"<script>",
        " update `game_record` ",
        " <trim prefix=\"set\" suffixOverrides=\",\"> ",
        "     <if test=\"pId != null\">`p_id` = #{pId},</if> ",
        "     <if test=\"channelId != null\">`channel_id` = #{channelId},</if> ",
        "     <if test=\"gameId != null\">`game_id` = #{gameId},</if> ",
        "     <if test=\"userId != null\">`user_id` = #{userId},</if> ",
        "     <if test=\"playerId != null\">`player_id` = #{playerId},</if> ",
        "     <if test=\"playerName != null\">`player_name` = #{playerName},</if> ",
        "     <if test=\"betType != null\">`bet_type` = #{betType},</if> ",
        "     <if test=\"betSubType != null\">`bet_sub_type` = #{betSubType},</if> ",
        "     <if test=\"betId != null\">`bet_id` = #{betId},</if> ",
        "     <if test=\"betSerialNo != null\">`bet_serial_no` = #{betSerialNo},</if> ",
        "     <if test=\"betAmount != null\">`bet_amount` = #{betAmount},</if> ",
        "     <if test=\"payOutAmount != null\">`pay_out_amount` = #{payOutAmount},</if> ",
        "     <if test=\"marketFlag != null\">`market_flag` = #{marketFlag},</if> ",
        "     <if test=\"status != null\">`status` = #{status},</if> ",
        "     <if test=\"freeSpinsFlag != null\">`free_spins_flag` = #{freeSpinsFlag},</if> ",
        "     <if test=\"freeSpinsTimes != null\">`free_spins_times` = #{freeSpinsTimes},</if> ",
        "     <if test=\"detail != null\">`detail` = #{detail},</if> ",
        "     <if test=\"creator != null\">`creator` = #{creator},</if> ",
        "     <if test=\"updater != null\">`updater` = #{updater},</if> ",
        "     `update_time` = current_timestamp() ",
        " </trim> ",
        " where id = #{id} limit 1",
        "</script>"})
    int updateById(BetRecordPo gameRecord);

    /** 查 */
    @Select("select * from `game_record` where `id` = #{id} limit 1")
    BetRecordPo selectById(@Param("id") Long id);

    @Select({"<script>",
            " select * from `game_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.pId != null and query.pId != ''\">and p_id like CONCAT('%', #{query.pId}, '%')</if> ",
            "     <if test=\"query.channelId != null and query.channelId != ''\">and channel_id like CONCAT('%', #{query.channelId}, '%')</if> ",
            "     <if test=\"query.gameId != null and query.gameId != ''\">and game_id like CONCAT('%', #{query.gameId}, '%')</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.playerName != null and query.playerName != ''\">and player_name like CONCAT('%', #{query.playerName}, '%')</if> ",
            "     <if test=\"query.betType != null\">and `bet_type` = #{query.betType}</if> ",
            "     <if test=\"query.betSubType != null\">and `bet_sub_type` = #{query.betSubType}</if> ",
            "     <if test=\"query.betId != null and query.betId != ''\">and bet_id like CONCAT('%', #{query.betId}, '%')</if> ",
            "     <if test=\"query.betSerialNo != null and query.betSerialNo != ''\">and bet_serial_no like CONCAT('%', #{query.betSerialNo}, '%')</if> ",
            "     <if test=\"query.betAmount != null\">and `bet_amount` = #{query.betAmount}</if> ",
            "     <if test=\"query.payOutAmount != null\">and `pay_out_amount` = #{query.payOutAmount}</if> ",
            "     <if test=\"query.marketFlag != null\">and `market_flag` = #{query.marketFlag}</if> ",
            "     <if test=\"query.status != null\">and `status` = #{query.status}</if> ",
            "     <if test=\"query.freeSpinsFlag != null\">and `free_spins_flag` = #{query.freeSpinsFlag}</if> ",
            "     <if test=\"query.freeSpinsTimes != null\">and `free_spins_times` = #{query.freeSpinsTimes}</if> ",
            "     <if test=\"query.detail != null and query.detail != ''\">and detail like CONCAT('%', #{query.detail}, '%')</if> ",
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
    BetRecordPo selectOne(@Param("query") BetRecordQuery query);

    @Select({"<script>",
            " select id from `game_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.pId != null and query.pId != ''\">and p_id like CONCAT('%', #{query.pId}, '%')</if> ",
            "     <if test=\"query.channelId != null and query.channelId != ''\">and channel_id like CONCAT('%', #{query.channelId}, '%')</if> ",
            "     <if test=\"query.gameId != null and query.gameId != ''\">and game_id like CONCAT('%', #{query.gameId}, '%')</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.playerName != null and query.playerName != ''\">and player_name like CONCAT('%', #{query.playerName}, '%')</if> ",
            "     <if test=\"query.betType != null\">and `bet_type` = #{query.betType}</if> ",
            "     <if test=\"query.betSubType != null\">and `bet_sub_type` = #{query.betSubType}</if> ",
            "     <if test=\"query.betId != null and query.betId != ''\">and bet_id like CONCAT('%', #{query.betId}, '%')</if> ",
            "     <if test=\"query.betSerialNo != null and query.betSerialNo != ''\">and bet_serial_no like CONCAT('%', #{query.betSerialNo}, '%')</if> ",
            "     <if test=\"query.betAmount != null\">and `bet_amount` = #{query.betAmount}</if> ",
            "     <if test=\"query.payOutAmount != null\">and `pay_out_amount` = #{query.payOutAmount}</if> ",
            "     <if test=\"query.marketFlag != null\">and `market_flag` = #{query.marketFlag}</if> ",
            "     <if test=\"query.status != null\">and `status` = #{query.status}</if> ",
            "     <if test=\"query.freeSpinsFlag != null\">and `free_spins_flag` = #{query.freeSpinsFlag}</if> ",
            "     <if test=\"query.freeSpinsTimes != null\">and `free_spins_times` = #{query.freeSpinsTimes}</if> ",
            "     <if test=\"query.detail != null and query.detail != ''\">and detail like CONCAT('%', #{query.detail}, '%')</if> ",
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
    Long selectId(@Param("query") BetRecordQuery query);

    @Select({"<script>",
            " select * from `game_record` where `id` in ",
            "   <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
            "     #{item} ",
            "   </foreach> ",
            "</script>"})
    List<BetRecordPo> listByIds(@Param("ids") List<Long> ids);

    @Select("select * from `game_record` order by `create_time` desc")
    List<BetRecordPo> listAll();

    @Select({"<script>",
            " select count(1) from `game_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.pId != null and query.pId != ''\">and p_id like CONCAT('%', #{query.pId}, '%')</if> ",
            "     <if test=\"query.channelId != null and query.channelId != ''\">and channel_id like CONCAT('%', #{query.channelId}, '%')</if> ",
            "     <if test=\"query.gameId != null and query.gameId != ''\">and game_id like CONCAT('%', #{query.gameId}, '%')</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.playerName != null and query.playerName != ''\">and player_name like CONCAT('%', #{query.playerName}, '%')</if> ",
            "     <if test=\"query.betType != null\">and `bet_type` = #{query.betType}</if> ",
            "     <if test=\"query.betSubType != null\">and `bet_sub_type` = #{query.betSubType}</if> ",
            "     <if test=\"query.betId != null and query.betId != ''\">and bet_id like CONCAT('%', #{query.betId}, '%')</if> ",
            "     <if test=\"query.betSerialNo != null and query.betSerialNo != ''\">and bet_serial_no like CONCAT('%', #{query.betSerialNo}, '%')</if> ",
            "     <if test=\"query.betAmount != null\">and `bet_amount` = #{query.betAmount}</if> ",
            "     <if test=\"query.payOutAmount != null\">and `pay_out_amount` = #{query.payOutAmount}</if> ",
            "     <if test=\"query.marketFlag != null\">and `market_flag` = #{query.marketFlag}</if> ",
            "     <if test=\"query.status != null\">and `status` = #{query.status}</if> ",
            "     <if test=\"query.freeSpinsFlag != null\">and `free_spins_flag` = #{query.freeSpinsFlag}</if> ",
            "     <if test=\"query.freeSpinsTimes != null\">and `free_spins_times` = #{query.freeSpinsTimes}</if> ",
            "     <if test=\"query.detail != null and query.detail != ''\">and detail like CONCAT('%', #{query.detail}, '%')</if> ",
            "     <if test=\"query.creator != null and query.creator != ''\">and creator like CONCAT('%', #{query.creator}, '%')</if> ",
            "     <if test=\"query.updater != null and query.updater != ''\">and updater like CONCAT('%', #{query.updater}, '%')</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "</script>"})
    long count(@Param("query") BetRecordQuery query);

    @Select({"<script>",
            " select * from `game_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.pId != null and query.pId != ''\">and p_id like CONCAT('%', #{query.pId}, '%')</if> ",
            "     <if test=\"query.channelId != null and query.channelId != ''\">and channel_id like CONCAT('%', #{query.channelId}, '%')</if> ",
            "     <if test=\"query.gameId != null and query.gameId != ''\">and game_id like CONCAT('%', #{query.gameId}, '%')</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.playerName != null and query.playerName != ''\">and player_name like CONCAT('%', #{query.playerName}, '%')</if> ",
            "     <if test=\"query.betType != null\">and `bet_type` = #{query.betType}</if> ",
            "     <if test=\"query.betSubType != null\">and `bet_sub_type` = #{query.betSubType}</if> ",
            "     <if test=\"query.betId != null and query.betId != ''\">and bet_id like CONCAT('%', #{query.betId}, '%')</if> ",
            "     <if test=\"query.betSerialNo != null and query.betSerialNo != ''\">and bet_serial_no like CONCAT('%', #{query.betSerialNo}, '%')</if> ",
            "     <if test=\"query.betAmount != null\">and `bet_amount` = #{query.betAmount}</if> ",
            "     <if test=\"query.payOutAmount != null\">and `pay_out_amount` = #{query.payOutAmount}</if> ",
            "     <if test=\"query.marketFlag != null\">and `market_flag` = #{query.marketFlag}</if> ",
            "     <if test=\"query.status != null\">and `status` = #{query.status}</if> ",
            "     <if test=\"query.freeSpinsFlag != null\">and `free_spins_flag` = #{query.freeSpinsFlag}</if> ",
            "     <if test=\"query.freeSpinsTimes != null\">and `free_spins_times` = #{query.freeSpinsTimes}</if> ",
            "     <if test=\"query.detail != null and query.detail != ''\">and detail like CONCAT('%', #{query.detail}, '%')</if> ",
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
    List<BetRecordPo> list(@Param("query") BetRecordQuery query);


    @Select({"<script>",
            " select id from `game_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.pId != null and query.pId != ''\">and p_id like CONCAT('%', #{query.pId}, '%')</if> ",
            "     <if test=\"query.channelId != null and query.channelId != ''\">and channel_id like CONCAT('%', #{query.channelId}, '%')</if> ",
            "     <if test=\"query.gameId != null and query.gameId != ''\">and game_id like CONCAT('%', #{query.gameId}, '%')</if> ",
            "     <if test=\"query.userId != null\">and `user_id` = #{query.userId}</if> ",
            "     <if test=\"query.playerId != null and query.playerId != ''\">and player_id like CONCAT('%', #{query.playerId}, '%')</if> ",
            "     <if test=\"query.playerName != null and query.playerName != ''\">and player_name like CONCAT('%', #{query.playerName}, '%')</if> ",
            "     <if test=\"query.betType != null\">and `bet_type` = #{query.betType}</if> ",
            "     <if test=\"query.betSubType != null\">and `bet_sub_type` = #{query.betSubType}</if> ",
            "     <if test=\"query.betId != null and query.betId != ''\">and bet_id like CONCAT('%', #{query.betId}, '%')</if> ",
            "     <if test=\"query.betSerialNo != null and query.betSerialNo != ''\">and bet_serial_no like CONCAT('%', #{query.betSerialNo}, '%')</if> ",
            "     <if test=\"query.betAmount != null\">and `bet_amount` = #{query.betAmount}</if> ",
            "     <if test=\"query.payOutAmount != null\">and `pay_out_amount` = #{query.payOutAmount}</if> ",
            "     <if test=\"query.marketFlag != null\">and `market_flag` = #{query.marketFlag}</if> ",
            "     <if test=\"query.status != null\">and `status` = #{query.status}</if> ",
            "     <if test=\"query.freeSpinsFlag != null\">and `free_spins_flag` = #{query.freeSpinsFlag}</if> ",
            "     <if test=\"query.freeSpinsTimes != null\">and `free_spins_times` = #{query.freeSpinsTimes}</if> ",
            "     <if test=\"query.detail != null and query.detail != ''\">and detail like CONCAT('%', #{query.detail}, '%')</if> ",
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
    List<Long> listId(@Param("query") BetRecordQuery query);
}

