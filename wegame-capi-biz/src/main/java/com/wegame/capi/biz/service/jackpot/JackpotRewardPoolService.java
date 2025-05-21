package com.wegame.capi.biz.service.jackpot;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.jackpot.JackpotRewardPoolConverter;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.dao.mapper.business.JackpotRewardPoolMapper;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolQuery;
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
public class JackpotRewardPoolService {

    @Autowired
    JackpotRewardPoolMapper jackpotRewardPoolMapper;

    /**
     * 新增
     *
     * @param jackpotRewardPoolDo jackpot奖池
     * @return 主键id
     */
    public Long add(JackpotRewardPoolDo jackpotRewardPoolDo) {
        AssertUtil.assertNotNull(jackpotRewardPoolDo, ErrorCode.ERR_PARAM);
        JackpotRewardPoolPo po = JackpotRewardPoolConverter.doToPo(jackpotRewardPoolDo, OptEnum.ADD);
        AssertUtil.assertNotNull(po, ErrorCode.ERR_PARAM);
        jackpotRewardPoolMapper.insert(po);
        return po.getId();
    }

    /**
     * 批量新增
     *
     * @param doList jackpot奖池列表
     * @return true/false
     */
    public boolean batchAdd(List<JackpotRewardPoolDo> doList) {
        AssertUtil.assertNotEmpty(doList, ErrorCode.ERR_PARAM);
        jackpotRewardPoolMapper.batchInsert(JackpotRewardPoolConverter.doToPoList(doList, OptEnum.ADD));
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
        jackpotRewardPoolMapper.deleteById(id);
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
        jackpotRewardPoolMapper.deleteByIds(ids);
        return true;
    }

    /**
     * 修改
     *
     * @param jackpotRewardPoolDo jackpot奖池
     * @return true/false
     */
    public boolean update(JackpotRewardPoolDo jackpotRewardPoolDo) {
        AssertUtil.assertNotNull(jackpotRewardPoolDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(jackpotRewardPoolDo.getId(), ErrorCode.ERR_PARAM);
        jackpotRewardPoolMapper.updateById(JackpotRewardPoolConverter.doToPo(jackpotRewardPoolDo, OptEnum.UPDATE));
        return true;
    }

    /**
     * 查询：根据主键id查询
     *
     * @param id 主键id
     * @return JackpotRewardPoolDo jackpot奖池
     */
    public JackpotRewardPoolDo get(Long id) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        return JackpotRewardPoolConverter.poToDo(jackpotRewardPoolMapper.selectById(id));
    }

    /**
     * 查询：根据条件查询
     *
     * @param query 查询条件
     * @return JackpotRewardPoolDo jackpot奖池
     */
    public JackpotRewardPoolDo selectOne(JackpotRewardPoolQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return JackpotRewardPoolConverter.poToDo(jackpotRewardPoolMapper.selectOne(query));
    }

    /**
     * 查询Id：根据条件查询
     *
     * @param query 查询条件
     * @return id
     */
     public Long selectId(JackpotRewardPoolQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return jackpotRewardPoolMapper.selectId(query);
     }

    /**
     * 查询：根据id列表
     *
     * @param ids id列表
     * @return List<JackpotRewardPoolDo> jackpot奖池列表
     */
    public List<JackpotRewardPoolDo> listByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return JackpotRewardPoolConverter.poToDoList(jackpotRewardPoolMapper.listByIds(ids));
    }

    /**
     * 查询全部
     *
     * @return List<JackpotRewardPoolDo> jackpot奖池列表
     */
    public List<JackpotRewardPoolDo> listAll() {
        return JackpotRewardPoolConverter.poToDoList(jackpotRewardPoolMapper.listAll());
    }

    /**
     * 查询id列表
     *
     * @param query 查询参数
     * @return List<Long> id列表
     */
    public List<Long> listId(JackpotRewardPoolQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return jackpotRewardPoolMapper.listId(query);
    }

    /**
     * 查询列表
     *
     * @param query 查询参数
     * @return List<JackpotRewardPoolDo> jackpot奖池列表
     */
    public List<JackpotRewardPoolDo> list(JackpotRewardPoolQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return JackpotRewardPoolConverter.poToDoList(jackpotRewardPoolMapper.list(query));
    }

    /**
     * 数量统计
     *
     * @param query 查询参数
     * @return 数量
     */
    public long count(JackpotRewardPoolQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return jackpotRewardPoolMapper.count(query);
    }

    ///////////////  above auto generated ///////////////
}