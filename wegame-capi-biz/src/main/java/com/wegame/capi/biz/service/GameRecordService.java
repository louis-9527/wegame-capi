//package com.wegame.capi.biz.service;
//
//import com.wegame.base.enums.ErrorCode;
//import com.wegame.base.enums.OptEnum;
//import com.wegame.base.utils.AssertUtil;
//import com.wegame.base.utils.DateUtil;
//import com.wegame.capi.api.SingleWalletApi;
//import com.wegame.capi.biz.converter.user.GameRecordConverter;
//import com.wegame.capi.biz.domain.user.GameRecordDo;
//import com.wegame.capi.client.domain.request.BetSummaryRequest;
//import com.wegame.capi.client.domain.vo.BetSummaryVo;
//import com.wegame.capi.dao.mapper.user.GameRecordMapper;
//import com.wegame.capi.dao.mapper.domain.user.GameRecordPo;
//import com.wegame.capi.dao.mapper.domain.user.GameRecordQuery;
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.List;
//
///**
// * po服务
// * @author genereate
// */
//@Service
//public class GameRecordService {
//
//    @Autowired
//    GameRecordMapper gameRecordMapper;
//    @Autowired
//    SingleWalletApi singleWalletApi;
//
//    /**
//     * 新增
//     *
//     * @param gameRecordDo 游戏记录
//     * @return 主键id
//     */
//    public Long add(GameRecordDo gameRecordDo) {
//        AssertUtil.assertNotNull(gameRecordDo, ErrorCode.ERR_PARAM);
//        GameRecordPo po = GameRecordConverter.doToPo(gameRecordDo, OptEnum.ADD);
//        AssertUtil.assertNotNull(po, ErrorCode.ERR_PARAM);
//        gameRecordMapper.insert(po);
//        return po.getId();
//    }
//
//    /**
//     * 批量新增
//     *
//     * @param doList 游戏记录列表
//     * @return true/false
//     */
//    public boolean batchAdd(List<GameRecordDo> doList) {
//        AssertUtil.assertNotEmpty(doList, ErrorCode.ERR_PARAM);
//        gameRecordMapper.batchInsert(GameRecordConverter.doToPoList(doList, OptEnum.ADD));
//        return true;
//    }
//
//    /**
//     * 删除：物理删除
//     *
//     * @param id 主键id
//     * @return true/false
//     */
//    public boolean delete(Long id) {
//        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
//        gameRecordMapper.deleteById(id);
//        return true;
//    }
//
//    /**
//     * 删除：批量物理删除
//     *
//     * @param ids 主键id列表
//     * @return true/false
//     */
//    public boolean batchDelete(List<Long> ids) {
//        AssertUtil.assertNotEmpty(ids, ErrorCode.ERR_PARAM);
//        gameRecordMapper.deleteByIds(ids);
//        return true;
//    }
//
//    /**
//     * 修改
//     *
//     * @param gameRecordDo 游戏记录
//     * @return true/false
//     */
//    public boolean update(GameRecordDo gameRecordDo) {
//        AssertUtil.assertNotNull(gameRecordDo, ErrorCode.ERR_PARAM);
//        AssertUtil.assertNotNull(gameRecordDo.getId(), ErrorCode.ERR_PARAM);
//        gameRecordMapper.updateById(GameRecordConverter.doToPo(gameRecordDo, OptEnum.UPDATE));
//        return true;
//    }
//
//    /**
//     * 查询：根据主键id查询
//     *
//     * @param id 主键id
//     * @return GameRecordDo 游戏记录
//     */
//    public GameRecordDo get(Long id) {
//        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
//        return GameRecordConverter.poToDo(gameRecordMapper.selectById(id));
//    }
//
//    /**
//     * 查询：根据条件查询
//     *
//     * @param query 查询条件
//     * @return GameRecordDo 游戏记录
//     */
//    public GameRecordDo selectOne(GameRecordQuery query) {
//        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
//        return GameRecordConverter.poToDo(gameRecordMapper.selectOne(query));
//    }
//
//    /**
//     * 查询Id：根据条件查询
//     *
//     * @param query 查询条件
//     * @return id
//     */
//     public Long selectId(GameRecordQuery query) {
//        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
//        return gameRecordMapper.selectId(query);
//     }
//
//    /**
//     * 查询：根据id列表
//     *
//     * @param ids id列表
//     * @return List<GameRecordDo> 游戏记录列表
//     */
//    public List<GameRecordDo> listByIds(List<Long> ids) {
//        if (CollectionUtils.isEmpty(ids)) {
//            return Collections.emptyList();
//        }
//        return GameRecordConverter.poToDoList(gameRecordMapper.listByIds(ids));
//    }
//
//    /**
//     * 查询全部
//     *
//     * @return List<GameRecordDo> 游戏记录列表
//     */
//    public List<GameRecordDo> listAll() {
//        return GameRecordConverter.poToDoList(gameRecordMapper.listAll());
//    }
//
//    /**
//     * 查询id列表
//     *
//     * @param query 查询参数
//     * @return List<Long> id列表
//     */
//    public List<Long> listId(GameRecordQuery query) {
//        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
//        return gameRecordMapper.listId(query);
//    }
//
//    /**
//     * 查询列表
//     *
//     * @param query 查询参数
//     * @return List<GameRecordDo> 游戏记录列表
//     */
//    public List<GameRecordDo> list(GameRecordQuery query) {
//        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
//        return GameRecordConverter.poToDoList(gameRecordMapper.list(query));
//    }
//
//    /**
//     * 数量统计
//     *
//     * @param query 查询参数
//     * @return 数量
//     */
//    public long count(GameRecordQuery query) {
//        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
//        return gameRecordMapper.count(query);
//    }
//
//    ///////////////  above auto generated ///////////////
//
//    public BetSummaryVo betSummary(BetSummaryRequest request) {
//        String hash = singleWalletApi.hash(request.getChannelId(), request.getHash(), request.getRefId(), DateUtil.toUTC(request.getCreateTime()));
//        AssertUtil.assertTrue(StringUtils.equals(hash, request.getHash()), ErrorCode.ERR_PARAM);
//        // todo 麦冬 统计结果
//        return BetSummaryVo.builder().build();
//    }
//
//}