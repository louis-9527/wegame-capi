package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.user.GameRecordDetailConverter;
import com.wegame.capi.biz.domain.user.GameRecordDetailDo;
import com.wegame.capi.dao.mapper.user.GameRecordDetailMapper;
import com.wegame.capi.dao.mapper.domain.user.GameRecordDetailPo;
import com.wegame.capi.dao.mapper.domain.user.GameRecordDetailQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * po服务
 * @author genereate
 */
@Service
public class GameRecordDetailService {

    @Autowired
    GameRecordDetailMapper gameRecordDetailMapper;

    /**
     * 新增
     *
     * @param gameRecordDetailDo 游戏记录明细
     * @return 主键id
     */
    public Long add(GameRecordDetailDo gameRecordDetailDo) {
        AssertUtil.assertNotNull(gameRecordDetailDo, ErrorCode.ERR_PARAM);
        GameRecordDetailPo po = GameRecordDetailConverter.doToPo(gameRecordDetailDo, OptEnum.ADD);
        AssertUtil.assertNotNull(po, ErrorCode.ERR_PARAM);
        gameRecordDetailMapper.insert(po);
        return po.getId();
    }

    /**
     * 批量新增
     *
     * @param doList 游戏记录明细列表
     * @return true/false
     */
    public boolean batchAdd(List<GameRecordDetailDo> doList) {
        AssertUtil.assertNotEmpty(doList, ErrorCode.ERR_PARAM);
        gameRecordDetailMapper.batchInsert(GameRecordDetailConverter.doToPoList(doList, OptEnum.ADD));
        return true;
    }

    /**
     * 删除：物理删除
     *
     * @param id 主键id
     * @return true/false
     */
    public boolean delete(Long id) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        gameRecordDetailMapper.deleteById(id);
        return true;
    }

    /**
     * 删除：批量物理删除
     *
     * @param ids 主键id列表
     * @return true/false
     */
    public boolean batchDelete(List<Long> ids) {
        AssertUtil.assertNotEmpty(ids, ErrorCode.ERR_PARAM);
        gameRecordDetailMapper.deleteByIds(ids);
        return true;
    }

    /**
     * 修改
     *
     * @param gameRecordDetailDo 游戏记录明细
     * @return true/false
     */
    public boolean update(GameRecordDetailDo gameRecordDetailDo) {
        AssertUtil.assertNotNull(gameRecordDetailDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(gameRecordDetailDo.getId(), ErrorCode.ERR_PARAM);
        gameRecordDetailMapper.updateById(GameRecordDetailConverter.doToPo(gameRecordDetailDo, OptEnum.UPDATE));
        return true;
    }

    /**
     * 查询：根据主键id查询
     *
     * @param id 主键id
     * @return GameRecordDetailDo 游戏记录明细
     */
    public GameRecordDetailDo get(Long id) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        return GameRecordDetailConverter.poToDo(gameRecordDetailMapper.selectById(id));
    }

    /**
     * 查询：根据条件查询
     *
     * @param query 查询条件
     * @return GameRecordDetailDo 游戏记录明细
     */
    public GameRecordDetailDo selectOne(GameRecordDetailQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return GameRecordDetailConverter.poToDo(gameRecordDetailMapper.selectOne(query));
    }

    /**
     * 查询Id：根据条件查询
     *
     * @param query 查询条件
     * @return id
     */
     public Long selectId(GameRecordDetailQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return gameRecordDetailMapper.selectId(query);
     }

    /**
     * 查询：根据id列表
     *
     * @param ids id列表
     * @return List<GameRecordDetailDo> 游戏记录明细列表
     */
    public List<GameRecordDetailDo> listByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return GameRecordDetailConverter.poToDoList(gameRecordDetailMapper.listByIds(ids));
    }

    /**
     * 查询全部
     *
     * @return List<GameRecordDetailDo> 游戏记录明细列表
     */
    public List<GameRecordDetailDo> listAll() {
        return GameRecordDetailConverter.poToDoList(gameRecordDetailMapper.listAll());
    }

    /**
     * 查询id列表
     *
     * @param query 查询参数
     * @return List<Long> id列表
     */
    public List<Long> listId(GameRecordDetailQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return gameRecordDetailMapper.listId(query);
    }

    /**
     * 查询列表
     *
     * @param query 查询参数
     * @return List<GameRecordDetailDo> 游戏记录明细列表
     */
    public List<GameRecordDetailDo> list(GameRecordDetailQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return GameRecordDetailConverter.poToDoList(gameRecordDetailMapper.list(query));
    }

    /**
     * 数量统计
     *
     * @param query 查询参数
     * @return 数量
     */
    public long count(GameRecordDetailQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return gameRecordDetailMapper.count(query);
    }

    ///////////////  above auto generated ///////////////
}