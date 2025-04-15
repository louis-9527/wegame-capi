package com.wegame.capi.biz.service.backend;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.backend.GameMaintainConfigConverter;
import com.wegame.capi.biz.domain.backend.GameMaintainConfigDo;
import com.wegame.capi.dao.mapper.backend.GameMaintainConfigMapper;
import com.wegame.capi.dao.mapper.domain.backend.GameMaintainConfigPo;
import com.wegame.capi.dao.mapper.domain.backend.GameMaintainConfigQuery;
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
public class GameMaintainConfigService {

    @Autowired
    GameMaintainConfigMapper gameMaintainConfigMapper;

    /**
     * 新增
     *
     * @param gameMaintainConfigDo 游戏维护配置
     * @return 主键id
     */
    public Long add(GameMaintainConfigDo gameMaintainConfigDo) {
        AssertUtil.assertNotNull(gameMaintainConfigDo, ErrorCode.ERR_PARAM);
        GameMaintainConfigPo po = GameMaintainConfigConverter.doToPo(gameMaintainConfigDo, OptEnum.ADD);
        AssertUtil.assertNotNull(po, ErrorCode.ERR_PARAM);
        gameMaintainConfigMapper.insert(po);
        return po.getId();
    }

    /**
     * 批量新增
     *
     * @param doList 游戏维护配置列表
     * @return true/false
     */
    public boolean batchAdd(List<GameMaintainConfigDo> doList) {
        AssertUtil.assertNotEmpty(doList, ErrorCode.ERR_PARAM);
        gameMaintainConfigMapper.batchInsert(GameMaintainConfigConverter.doToPoList(doList, OptEnum.ADD));
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
        gameMaintainConfigMapper.deleteById(id);
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
        gameMaintainConfigMapper.deleteByIds(ids);
        return true;
    }

    /**
     * 修改
     *
     * @param gameMaintainConfigDo 游戏维护配置
     * @return true/false
     */
    public boolean update(GameMaintainConfigDo gameMaintainConfigDo) {
        AssertUtil.assertNotNull(gameMaintainConfigDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(gameMaintainConfigDo.getId(), ErrorCode.ERR_PARAM);
        gameMaintainConfigMapper.updateById(GameMaintainConfigConverter.doToPo(gameMaintainConfigDo, OptEnum.UPDATE));
        return true;
    }

    /**
     * 查询：根据主键id查询
     *
     * @param id 主键id
     * @return GameMaintainConfigDo 游戏维护配置
     */
    public GameMaintainConfigDo get(Long id) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        return GameMaintainConfigConverter.poToDo(gameMaintainConfigMapper.selectById(id));
    }

    /**
     * 查询：根据条件查询
     *
     * @param query 查询条件
     * @return GameMaintainConfigDo 游戏维护配置
     */
    public GameMaintainConfigDo selectOne(GameMaintainConfigQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return GameMaintainConfigConverter.poToDo(gameMaintainConfigMapper.selectOne(query));
    }

    /**
     * 查询Id：根据条件查询
     *
     * @param query 查询条件
     * @return id
     */
     public Long selectId(GameMaintainConfigQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return gameMaintainConfigMapper.selectId(query);
     }

    /**
     * 查询：根据id列表
     *
     * @param ids id列表
     * @return List<GameMaintainConfigDo> 游戏维护配置列表
     */
    public List<GameMaintainConfigDo> listByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return GameMaintainConfigConverter.poToDoList(gameMaintainConfigMapper.listByIds(ids));
    }

    /**
     * 查询全部
     *
     * @return List<GameMaintainConfigDo> 游戏维护配置列表
     */
    public List<GameMaintainConfigDo> listAll() {
        return GameMaintainConfigConverter.poToDoList(gameMaintainConfigMapper.listAll());
    }

    /**
     * 查询id列表
     *
     * @param query 查询参数
     * @return List<Long> id列表
     */
    public List<Long> listId(GameMaintainConfigQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return gameMaintainConfigMapper.listId(query);
    }

    /**
     * 查询列表
     *
     * @param query 查询参数
     * @return List<GameMaintainConfigDo> 游戏维护配置列表
     */
    public List<GameMaintainConfigDo> list(GameMaintainConfigQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return GameMaintainConfigConverter.poToDoList(gameMaintainConfigMapper.list(query));
    }

    /**
     * 数量统计
     *
     * @param query 查询参数
     * @return 数量
     */
    public long count(GameMaintainConfigQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return gameMaintainConfigMapper.count(query);
    }

    ///////////////  above auto generated ///////////////
}