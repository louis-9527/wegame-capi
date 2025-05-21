package com.wegame.capi.dao.mapper.business;

import com.wegame.capi.dao.mapper.domain.business.JackpotRewardPoolInputRecordPo;
import com.wegame.capi.dao.mapper.domain.business.JackpotRewardPoolInputRecordQuery;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * jackpot_reward_pool_input_record：jackpot奖池流入记录
 * 方法顺序：增删改查
 * @author generate
 */
@DS("wegame_business_db")
@Mapper
public interface JackpotRewardPoolInputRecordMapper {
    /** 增 */
    @Insert({"insert into `jackpot_reward_pool_input_record`(" +
            " `type`, " +
            " `grand_amount`, " +
            " `major_amount`, " +
            " `minor_amount`, " +
            " `mini_amount`, " +
            " `creator`, " +
            " `updater`, " +
            " `create_time`, " +
            " `update_time` " +
            ") values (" +
            " #{type}, " +
            " #{grandAmount}, " +
            " #{majorAmount}, " +
            " #{minorAmount}, " +
            " #{miniAmount}, " +
            " #{creator}, " +
            " #{updater}, " +
            " current_timestamp(), " +
            " current_timestamp()" +
            ")"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(JackpotRewardPoolInputRecordPo jackpotRewardPoolInputRecord);

    @Insert({"<script>",
        " insert into `jackpot_reward_pool_input_record` (",
        "   `type`, ",
        "   `grand_amount`, ",
        "   `major_amount`, ",
        "   `minor_amount`, ",
        "   `mini_amount`, ",
        "   `creator`, ",
        "   `updater`, ",
        "   `create_time`, ",
        "   `update_time` ",
        ") values ",
        " <foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\" nullable=\"true\"> ",
        " (",
        "   #{item.type}, ",
        "   #{item.grandAmount}, ",
        "   #{item.majorAmount}, ",
        "   #{item.minorAmount}, ",
        "   #{item.miniAmount}, ",
        "   #{item.creator}, ",
        "   #{item.updater}, ",
        "   current_timestamp(), ",
        "   current_timestamp()",
        " )",
        " </foreach> ",
        "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int batchInsert(@Param("list") List<JackpotRewardPoolInputRecordPo> list);

    /** 删 */
    @Delete("delete from `jackpot_reward_pool_input_record` where `id` = #{id} limit 1")
    int deleteById(@Param("id") Long id);

    @Delete({"<script>",
        " delete from `jackpot_reward_pool_input_record` where `id` in ",
        " <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
        "   #{item} ",
        " </foreach> ",
        "</script>"})
    int deleteByIds(@Param("ids") List<Long> ids);

    /** 改 */
    @Update({"<script>",
        " update `jackpot_reward_pool_input_record` ",
        " <trim prefix=\"set\" suffixOverrides=\",\"> ",
        "     <if test=\"type != null\">`type` = #{type},</if> ",
        "     <if test=\"grandAmount != null\">`grand_amount` = #{grandAmount},</if> ",
        "     <if test=\"majorAmount != null\">`major_amount` = #{majorAmount},</if> ",
        "     <if test=\"minorAmount != null\">`minor_amount` = #{minorAmount},</if> ",
        "     <if test=\"miniAmount != null\">`mini_amount` = #{miniAmount},</if> ",
        "     <if test=\"creator != null\">`creator` = #{creator},</if> ",
        "     <if test=\"updater != null\">`updater` = #{updater},</if> ",
        "     `update_time` = current_timestamp() ",
        " </trim> ",
        " where id = #{id} limit 1",
        "</script>"})
    int updateById(JackpotRewardPoolInputRecordPo jackpotRewardPoolInputRecord);

    /** 查 */
    @Select("select * from `jackpot_reward_pool_input_record` where `id` = #{id} limit 1")
    JackpotRewardPoolInputRecordPo selectById(@Param("id") Long id);

    @Select({"<script>",
            " select * from `jackpot_reward_pool_input_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.type != null\">and `type` = #{query.type}</if> ",
            "     <if test=\"query.grandAmount != null\">and `grand_amount` = #{query.grandAmount}</if> ",
            "     <if test=\"query.majorAmount != null\">and `major_amount` = #{query.majorAmount}</if> ",
            "     <if test=\"query.minorAmount != null\">and `minor_amount` = #{query.minorAmount}</if> ",
            "     <if test=\"query.miniAmount != null\">and `mini_amount` = #{query.miniAmount}</if> ",
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
    JackpotRewardPoolInputRecordPo selectOne(@Param("query") JackpotRewardPoolInputRecordQuery query);

    @Select({"<script>",
            " select id from `jackpot_reward_pool_input_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.type != null\">and `type` = #{query.type}</if> ",
            "     <if test=\"query.grandAmount != null\">and `grand_amount` = #{query.grandAmount}</if> ",
            "     <if test=\"query.majorAmount != null\">and `major_amount` = #{query.majorAmount}</if> ",
            "     <if test=\"query.minorAmount != null\">and `minor_amount` = #{query.minorAmount}</if> ",
            "     <if test=\"query.miniAmount != null\">and `mini_amount` = #{query.miniAmount}</if> ",
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
    Long selectId(@Param("query") JackpotRewardPoolInputRecordQuery query);

    @Select({"<script>",
            " select * from `jackpot_reward_pool_input_record` where `id` in ",
            "   <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
            "     #{item} ",
            "   </foreach> ",
            "</script>"})
    List<JackpotRewardPoolInputRecordPo> listByIds(@Param("ids") List<Long> ids);

    @Select("select * from `jackpot_reward_pool_input_record` order by `create_time` desc")
    List<JackpotRewardPoolInputRecordPo> listAll();

    @Select({"<script>",
            " select count(1) from `jackpot_reward_pool_input_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.type != null\">and `type` = #{query.type}</if> ",
            "     <if test=\"query.grandAmount != null\">and `grand_amount` = #{query.grandAmount}</if> ",
            "     <if test=\"query.majorAmount != null\">and `major_amount` = #{query.majorAmount}</if> ",
            "     <if test=\"query.minorAmount != null\">and `minor_amount` = #{query.minorAmount}</if> ",
            "     <if test=\"query.miniAmount != null\">and `mini_amount` = #{query.miniAmount}</if> ",
            "     <if test=\"query.creator != null and query.creator != ''\">and creator like CONCAT('%', #{query.creator}, '%')</if> ",
            "     <if test=\"query.updater != null and query.updater != ''\">and updater like CONCAT('%', #{query.updater}, '%')</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "</script>"})
    long count(@Param("query") JackpotRewardPoolInputRecordQuery query);

    @Select({"<script>",
            " select * from `jackpot_reward_pool_input_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.type != null\">and `type` = #{query.type}</if> ",
            "     <if test=\"query.grandAmount != null\">and `grand_amount` = #{query.grandAmount}</if> ",
            "     <if test=\"query.majorAmount != null\">and `major_amount` = #{query.majorAmount}</if> ",
            "     <if test=\"query.minorAmount != null\">and `minor_amount` = #{query.minorAmount}</if> ",
            "     <if test=\"query.miniAmount != null\">and `mini_amount` = #{query.miniAmount}</if> ",
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
    List<JackpotRewardPoolInputRecordPo> list(@Param("query") JackpotRewardPoolInputRecordQuery query);


    @Select({"<script>",
            " select id from `jackpot_reward_pool_input_record` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.type != null\">and `type` = #{query.type}</if> ",
            "     <if test=\"query.grandAmount != null\">and `grand_amount` = #{query.grandAmount}</if> ",
            "     <if test=\"query.majorAmount != null\">and `major_amount` = #{query.majorAmount}</if> ",
            "     <if test=\"query.minorAmount != null\">and `minor_amount` = #{query.minorAmount}</if> ",
            "     <if test=\"query.miniAmount != null\">and `mini_amount` = #{query.miniAmount}</if> ",
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
    List<Long> listId(@Param("query") JackpotRewardPoolInputRecordQuery query);
}

