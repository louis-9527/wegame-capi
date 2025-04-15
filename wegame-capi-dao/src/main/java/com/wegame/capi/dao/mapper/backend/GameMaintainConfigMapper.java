package com.wegame.capi.dao.mapper.backend;

import com.wegame.capi.dao.mapper.domain.backend.GameMaintainConfigPo;
import com.wegame.capi.dao.mapper.domain.backend.GameMaintainConfigQuery;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * game_maintain_config：游戏维护配置
 * 方法顺序：增删改查
 * @author generate
 */
@DS("wegame_backend_db")
@Mapper
public interface GameMaintainConfigMapper {
    /** 增 */
    @Insert({"insert into `game_maintain_config`(" +
            " `game_id`, " +
            " `maintain_start_time`, " +
            " `maintain_end_time`, " +
            " `enable_status`, " +
            " `creator`, " +
            " `updater`, " +
            " `create_time`, " +
            " `update_time` " +
            ") values (" +
            " #{gameId}, " +
            " #{maintainStartTime}, " +
            " #{maintainEndTime}, " +
            " #{enableStatus}, " +
            " #{creator}, " +
            " #{updater}, " +
            " current_timestamp(), " +
            " current_timestamp()" +
            ")"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(GameMaintainConfigPo gameMaintainConfig);

    @Insert({"<script>",
        " insert into `game_maintain_config` (",
        "   `game_id`, ",
        "   `maintain_start_time`, ",
        "   `maintain_end_time`, ",
        "   `enable_status`, ",
        "   `creator`, ",
        "   `updater`, ",
        "   `create_time`, ",
        "   `update_time` ",
        ") values ",
        " <foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\" nullable=\"true\"> ",
        " (",
        "   #{item.gameId}, ",
        "   #{item.maintainStartTime}, ",
        "   #{item.maintainEndTime}, ",
        "   #{item.enableStatus}, ",
        "   #{item.creator}, ",
        "   #{item.updater}, ",
        "   current_timestamp(), ",
        "   current_timestamp()",
        " )",
        " </foreach> ",
        "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int batchInsert(@Param("list") List<GameMaintainConfigPo> list);

    /** 删 */
    @Delete("delete from `game_maintain_config` where `id` = #{id} limit 1")
    int deleteById(@Param("id") Long id);

    @Delete({"<script>",
        " delete from `game_maintain_config` where `id` in ",
        " <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
        "   #{item} ",
        " </foreach> ",
        "</script>"})
    int deleteByIds(@Param("ids") List<Long> ids);

    /** 改 */
    @Update({"<script>",
        " update `game_maintain_config` ",
        " <trim prefix=\"set\" suffixOverrides=\",\"> ",
        "     <if test=\"gameId != null\">`game_id` = #{gameId},</if> ",
        "     <if test=\"maintainStartTime != null\">`maintain_start_time` = #{maintainStartTime},</if> ",
        "     <if test=\"maintainEndTime != null\">`maintain_end_time` = #{maintainEndTime},</if> ",
        "     <if test=\"enableStatus != null\">`enable_status` = #{enableStatus},</if> ",
        "     <if test=\"creator != null\">`creator` = #{creator},</if> ",
        "     <if test=\"updater != null\">`updater` = #{updater},</if> ",
        "     `update_time` = current_timestamp() ",
        " </trim> ",
        " where id = #{id} limit 1",
        "</script>"})
    int updateById(GameMaintainConfigPo gameMaintainConfig);

    /** 查 */
    @Select("select * from `game_maintain_config` where `id` = #{id} limit 1")
    GameMaintainConfigPo selectById(@Param("id") Long id);

    @Select({"<script>",
            " select * from `game_maintain_config` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.gameId != null and query.gameId != ''\">and game_id like CONCAT('%', #{query.gameId}, '%')</if> ",
            "     <if test=\"query.maintainStartTime != null\">and `maintain_start_time` = #{query.maintainStartTime}</if> ",
            "     <if test=\"query.maintainEndTime != null\">and `maintain_end_time` = #{query.maintainEndTime}</if> ",
            "     <if test=\"query.enableStatus != null\">and `enable_status` = #{query.enableStatus}</if> ",
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
    GameMaintainConfigPo selectOne(@Param("query") GameMaintainConfigQuery query);

    @Select({"<script>",
            " select id from `game_maintain_config` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.gameId != null and query.gameId != ''\">and game_id like CONCAT('%', #{query.gameId}, '%')</if> ",
            "     <if test=\"query.maintainStartTime != null\">and `maintain_start_time` = #{query.maintainStartTime}</if> ",
            "     <if test=\"query.maintainEndTime != null\">and `maintain_end_time` = #{query.maintainEndTime}</if> ",
            "     <if test=\"query.enableStatus != null\">and `enable_status` = #{query.enableStatus}</if> ",
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
    Long selectId(@Param("query") GameMaintainConfigQuery query);

    @Select({"<script>",
            " select * from `game_maintain_config` where `id` in ",
            "   <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
            "     #{item} ",
            "   </foreach> ",
            "</script>"})
    List<GameMaintainConfigPo> listByIds(@Param("ids") List<Long> ids);

    @Select("select * from `game_maintain_config` order by `create_time` desc")
    List<GameMaintainConfigPo> listAll();

    @Select({"<script>",
            " select count(1) from `game_maintain_config` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.gameId != null and query.gameId != ''\">and game_id like CONCAT('%', #{query.gameId}, '%')</if> ",
            "     <if test=\"query.maintainStartTime != null\">and `maintain_start_time` = #{query.maintainStartTime}</if> ",
            "     <if test=\"query.maintainEndTime != null\">and `maintain_end_time` = #{query.maintainEndTime}</if> ",
            "     <if test=\"query.enableStatus != null\">and `enable_status` = #{query.enableStatus}</if> ",
            "     <if test=\"query.creator != null and query.creator != ''\">and creator like CONCAT('%', #{query.creator}, '%')</if> ",
            "     <if test=\"query.updater != null and query.updater != ''\">and updater like CONCAT('%', #{query.updater}, '%')</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "</script>"})
    long count(@Param("query") GameMaintainConfigQuery query);

    @Select({"<script>",
            " select * from `game_maintain_config` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.gameId != null and query.gameId != ''\">and game_id like CONCAT('%', #{query.gameId}, '%')</if> ",
            "     <if test=\"query.maintainStartTime != null\">and `maintain_start_time` = #{query.maintainStartTime}</if> ",
            "     <if test=\"query.maintainEndTime != null\">and `maintain_end_time` = #{query.maintainEndTime}</if> ",
            "     <if test=\"query.enableStatus != null\">and `enable_status` = #{query.enableStatus}</if> ",
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
    List<GameMaintainConfigPo> list(@Param("query") GameMaintainConfigQuery query);


    @Select({"<script>",
            " select id from `game_maintain_config` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.gameId != null and query.gameId != ''\">and game_id like CONCAT('%', #{query.gameId}, '%')</if> ",
            "     <if test=\"query.maintainStartTime != null\">and `maintain_start_time` = #{query.maintainStartTime}</if> ",
            "     <if test=\"query.maintainEndTime != null\">and `maintain_end_time` = #{query.maintainEndTime}</if> ",
            "     <if test=\"query.enableStatus != null\">and `enable_status` = #{query.enableStatus}</if> ",
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
    List<Long> listId(@Param("query") GameMaintainConfigQuery query);
}

