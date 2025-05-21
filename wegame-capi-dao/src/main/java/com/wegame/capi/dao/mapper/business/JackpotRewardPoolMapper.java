package com.wegame.capi.dao.mapper.business;

import com.wegame.capi.dao.mapper.domain.business.JackpotRewardPoolPo;
import com.wegame.capi.dao.mapper.domain.business.JackpotRewardPoolQuery;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * jackpot_reward_pool：jackpot奖池
 * 方法顺序：增删改查
 * @author generate
 */
@DS("wegame_business_db")
@Mapper
public interface JackpotRewardPoolMapper {
    /** 增 */
    @Insert({"insert into `jackpot_reward_pool`(" +
            " `type`, " +
            " `amount`, " +
            " `creator`, " +
            " `updater`, " +
            " `create_time`, " +
            " `update_time` " +
            ") values (" +
            " #{type}, " +
            " #{amount}, " +
            " #{creator}, " +
            " #{updater}, " +
            " current_timestamp(), " +
            " current_timestamp()" +
            ")"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(JackpotRewardPoolPo jackpotRewardPool);

    @Insert({"<script>",
        " insert into `jackpot_reward_pool` (",
        "   `type`, ",
        "   `amount`, ",
        "   `creator`, ",
        "   `updater`, ",
        "   `create_time`, ",
        "   `update_time` ",
        ") values ",
        " <foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\" nullable=\"true\"> ",
        " (",
        "   #{item.type}, ",
        "   #{item.amount}, ",
        "   #{item.creator}, ",
        "   #{item.updater}, ",
        "   current_timestamp(), ",
        "   current_timestamp()",
        " )",
        " </foreach> ",
        "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int batchInsert(@Param("list") List<JackpotRewardPoolPo> list);

    /** 删 */
    @Delete("delete from `jackpot_reward_pool` where `id` = #{id} limit 1")
    int deleteById(@Param("id") Long id);

    @Delete({"<script>",
        " delete from `jackpot_reward_pool` where `id` in ",
        " <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
        "   #{item} ",
        " </foreach> ",
        "</script>"})
    int deleteByIds(@Param("ids") List<Long> ids);

    /** 改 */
    @Update({"<script>",
        " update `jackpot_reward_pool` ",
        " <trim prefix=\"set\" suffixOverrides=\",\"> ",
        "     <if test=\"type != null\">`type` = #{type},</if> ",
        "     <if test=\"amount != null\">`amount` = #{amount},</if> ",
        "     <if test=\"creator != null\">`creator` = #{creator},</if> ",
        "     <if test=\"updater != null\">`updater` = #{updater},</if> ",
        "     `update_time` = current_timestamp() ",
        " </trim> ",
        " where id = #{id} limit 1",
        "</script>"})
    int updateById(JackpotRewardPoolPo jackpotRewardPool);

    /** 查 */
    @Select("select * from `jackpot_reward_pool` where `id` = #{id} limit 1")
    JackpotRewardPoolPo selectById(@Param("id") Long id);

    @Select({"<script>",
            " select * from `jackpot_reward_pool` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.type != null\">and `type` = #{query.type}</if> ",
            "     <if test=\"query.amount != null\">and `amount` = #{query.amount}</if> ",
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
    JackpotRewardPoolPo selectOne(@Param("query") JackpotRewardPoolQuery query);

    @Select({"<script>",
            " select id from `jackpot_reward_pool` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.type != null\">and `type` = #{query.type}</if> ",
            "     <if test=\"query.amount != null\">and `amount` = #{query.amount}</if> ",
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
    Long selectId(@Param("query") JackpotRewardPoolQuery query);

    @Select({"<script>",
            " select * from `jackpot_reward_pool` where `id` in ",
            "   <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
            "     #{item} ",
            "   </foreach> ",
            "</script>"})
    List<JackpotRewardPoolPo> listByIds(@Param("ids") List<Long> ids);

    @Select("select * from `jackpot_reward_pool` order by `create_time` desc")
    List<JackpotRewardPoolPo> listAll();

    @Select({"<script>",
            " select count(1) from `jackpot_reward_pool` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.type != null\">and `type` = #{query.type}</if> ",
            "     <if test=\"query.amount != null\">and `amount` = #{query.amount}</if> ",
            "     <if test=\"query.creator != null and query.creator != ''\">and creator like CONCAT('%', #{query.creator}, '%')</if> ",
            "     <if test=\"query.updater != null and query.updater != ''\">and updater like CONCAT('%', #{query.updater}, '%')</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "</script>"})
    long count(@Param("query") JackpotRewardPoolQuery query);

    @Select({"<script>",
            " select * from `jackpot_reward_pool` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.type != null\">and `type` = #{query.type}</if> ",
            "     <if test=\"query.amount != null\">and `amount` = #{query.amount}</if> ",
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
    List<JackpotRewardPoolPo> list(@Param("query") JackpotRewardPoolQuery query);


    @Select({"<script>",
            " select id from `jackpot_reward_pool` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.type != null\">and `type` = #{query.type}</if> ",
            "     <if test=\"query.amount != null\">and `amount` = #{query.amount}</if> ",
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
    List<Long> listId(@Param("query") JackpotRewardPoolQuery query);
}

