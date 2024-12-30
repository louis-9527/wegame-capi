package com.wegame.capi.dao.mapper.user;

import com.wegame.capi.dao.mapper.domain.user.GameRecordDetailPo;
import com.wegame.capi.dao.mapper.domain.user.GameRecordDetailQuery;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * game_record_detail：游戏记录明细
 * 方法顺序：增删改查
 * @author generate
 */
@DS("wegame_business_db")
@Mapper
public interface GameRecordDetailMapper {
    /** 增 */
    @Insert({"insert into `game_record_detail`(" +
            " `bet_amount`, " +
            " `pay_out_amount`, " +
            " `game_result`, " +
            " `win_result`, " +
            " `odds`, " +
            " `multiple`, " +
            " `create_time`, " +
            " `update_time` " +
            ") values (" +
            " #{betAmount}, " +
            " #{payOutAmount}, " +
            " #{gameResult}, " +
            " #{winResult}, " +
            " #{odds}, " +
            " #{multiple}, " +
            " current_timestamp(), " +
            " current_timestamp()" +
            ")"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(GameRecordDetailPo gameRecordDetail);

    @Insert({"<script>",
        " insert into `game_record_detail` (",
        "   `bet_amount`, ",
        "   `pay_out_amount`, ",
        "   `game_result`, ",
        "   `win_result`, ",
        "   `odds`, ",
        "   `multiple`, ",
        "   `create_time`, ",
        "   `update_time` ",
        ") values ",
        " <foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\" nullable=\"true\"> ",
        " (",
        "   #{item.betAmount}, ",
        "   #{item.payOutAmount}, ",
        "   #{item.gameResult}, ",
        "   #{item.winResult}, ",
        "   #{item.odds}, ",
        "   #{item.multiple}, ",
        "   current_timestamp(), ",
        "   current_timestamp()",
        " )",
        " </foreach> ",
        "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int batchInsert(@Param("list") List<GameRecordDetailPo> list);

    /** 删 */
    @Delete("delete from `game_record_detail` where `id` = #{id} limit 1")
    int deleteById(@Param("id") Long id);

    @Delete({"<script>",
        " delete from `game_record_detail` where `id` in ",
        " <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
        "   #{item} ",
        " </foreach> ",
        "</script>"})
    int deleteByIds(@Param("ids") List<Long> ids);

    /** 改 */
    @Update({"<script>",
        " update `game_record_detail` ",
        " <trim prefix=\"set\" suffixOverrides=\",\"> ",
        "     <if test=\"betAmount != null\">`bet_amount` = #{betAmount},</if> ",
        "     <if test=\"payOutAmount != null\">`pay_out_amount` = #{payOutAmount},</if> ",
        "     <if test=\"gameResult != null\">`game_result` = #{gameResult},</if> ",
        "     <if test=\"winResult != null\">`win_result` = #{winResult},</if> ",
        "     <if test=\"odds != null\">`odds` = #{odds},</if> ",
        "     <if test=\"multiple != null\">`multiple` = #{multiple},</if> ",
        "     `update_time` = current_timestamp() ",
        " </trim> ",
        " where id = #{id} limit 1",
        "</script>"})
    int updateById(GameRecordDetailPo gameRecordDetail);

    /** 查 */
    @Select("select * from `game_record_detail` where `id` = #{id} limit 1")
    GameRecordDetailPo selectById(@Param("id") Long id);

    @Select({"<script>",
            " select * from `game_record_detail` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.betAmount != null\">and `bet_amount` = #{query.betAmount}</if> ",
            "     <if test=\"query.payOutAmount != null\">and `pay_out_amount` = #{query.payOutAmount}</if> ",
            "     <if test=\"query.gameResult != null and query.gameResult != ''\">and game_result like CONCAT('%', #{query.gameResult}, '%')</if> ",
            "     <if test=\"query.winResult != null and query.winResult != ''\">and win_result like CONCAT('%', #{query.winResult}, '%')</if> ",
            "     <if test=\"query.odds != null\">and `odds` = #{query.odds}</if> ",
            "     <if test=\"query.multiple != null\">and `multiple` = #{query.multiple}</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "     <if test=\"query.orderByClause != null and query.orderByClause != ''\"> ${query.orderByClause} </if> ",
            "     limit 1 ",
            "</script>"})
    GameRecordDetailPo selectOne(@Param("query") GameRecordDetailQuery query);

    @Select({"<script>",
            " select id from `game_record_detail` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.betAmount != null\">and `bet_amount` = #{query.betAmount}</if> ",
            "     <if test=\"query.payOutAmount != null\">and `pay_out_amount` = #{query.payOutAmount}</if> ",
            "     <if test=\"query.gameResult != null and query.gameResult != ''\">and game_result like CONCAT('%', #{query.gameResult}, '%')</if> ",
            "     <if test=\"query.winResult != null and query.winResult != ''\">and win_result like CONCAT('%', #{query.winResult}, '%')</if> ",
            "     <if test=\"query.odds != null\">and `odds` = #{query.odds}</if> ",
            "     <if test=\"query.multiple != null\">and `multiple` = #{query.multiple}</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "     <if test=\"query.orderByClause != null and query.orderByClause != ''\"> ${query.orderByClause} </if> ",
            "     limit 1 ",
            "</script>"})
    Long selectId(@Param("query") GameRecordDetailQuery query);

    @Select({"<script>",
            " select * from `game_record_detail` where `id` in ",
            "   <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\" nullable=\"true\"> ",
            "     #{item} ",
            "   </foreach> ",
            "</script>"})
    List<GameRecordDetailPo> listByIds(@Param("ids") List<Long> ids);

    @Select("select * from `game_record_detail` order by `create_time` desc")
    List<GameRecordDetailPo> listAll();

    @Select({"<script>",
            " select count(1) from `game_record_detail` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.betAmount != null\">and `bet_amount` = #{query.betAmount}</if> ",
            "     <if test=\"query.payOutAmount != null\">and `pay_out_amount` = #{query.payOutAmount}</if> ",
            "     <if test=\"query.gameResult != null and query.gameResult != ''\">and game_result like CONCAT('%', #{query.gameResult}, '%')</if> ",
            "     <if test=\"query.winResult != null and query.winResult != ''\">and win_result like CONCAT('%', #{query.winResult}, '%')</if> ",
            "     <if test=\"query.odds != null\">and `odds` = #{query.odds}</if> ",
            "     <if test=\"query.multiple != null\">and `multiple` = #{query.multiple}</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "</script>"})
    long count(@Param("query") GameRecordDetailQuery query);

    @Select({"<script>",
            " select * from `game_record_detail` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.betAmount != null\">and `bet_amount` = #{query.betAmount}</if> ",
            "     <if test=\"query.payOutAmount != null\">and `pay_out_amount` = #{query.payOutAmount}</if> ",
            "     <if test=\"query.gameResult != null and query.gameResult != ''\">and game_result like CONCAT('%', #{query.gameResult}, '%')</if> ",
            "     <if test=\"query.winResult != null and query.winResult != ''\">and win_result like CONCAT('%', #{query.winResult}, '%')</if> ",
            "     <if test=\"query.odds != null\">and `odds` = #{query.odds}</if> ",
            "     <if test=\"query.multiple != null\">and `multiple` = #{query.multiple}</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "     <if test=\"query.orderByClause != null and query.orderByClause != ''\"> ${query.orderByClause} </if> ",
            "     <if test=\"query.limit != null\"> limit #{query.limit}  </if> ",
            "     <if test=\"query.offset != null\"> offset #{query.offset}  </if> ",
            "</script>"})
    List<GameRecordDetailPo> list(@Param("query") GameRecordDetailQuery query);


    @Select({"<script>",
            " select id from `game_record_detail` ",
            " <where> ",
            "     <if test=\"query.id != null\"> `id` = #{query.id}</if> ",
            "     <if test=\"query.betAmount != null\">and `bet_amount` = #{query.betAmount}</if> ",
            "     <if test=\"query.payOutAmount != null\">and `pay_out_amount` = #{query.payOutAmount}</if> ",
            "     <if test=\"query.gameResult != null and query.gameResult != ''\">and game_result like CONCAT('%', #{query.gameResult}, '%')</if> ",
            "     <if test=\"query.winResult != null and query.winResult != ''\">and win_result like CONCAT('%', #{query.winResult}, '%')</if> ",
            "     <if test=\"query.odds != null\">and `odds` = #{query.odds}</if> ",
            "     <if test=\"query.multiple != null\">and `multiple` = #{query.multiple}</if> ",
            "     <if test=\"query.startCreateTime != null\">and `create_time` <![CDATA[>=]]> #{query.startCreateTime}</if> ",
            "     <if test=\"query.endCreateTime != null\">and `create_time` <![CDATA[<=]]> #{query.endCreateTime}</if> ",
            "     <if test=\"query.startUpdateTime != null\">and `update_time` <![CDATA[>=]]> #{query.startUpdateTime}</if> ",
            "     <if test=\"query.endUpdateTime != null\">and `update_time` <![CDATA[<=]]> #{query.endUpdateTime}</if> ",
            " </where> ",
            "     <if test=\"query.orderByClause != null and query.orderByClause != ''\"> ${query.orderByClause} </if> ",
            "     <if test=\"query.limit != null\"> limit #{query.limit}  </if> ",
            "     <if test=\"query.offset != null\"> offset #{query.offset}  </if> ",
            "</script>"})
    List<Long> listId(@Param("query") GameRecordDetailQuery query);
}

