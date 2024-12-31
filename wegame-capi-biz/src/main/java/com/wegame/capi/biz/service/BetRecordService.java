package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.SingleWalletApi;
import com.wegame.capi.biz.converter.user.BetRecordConverter;
import com.wegame.capi.biz.domain.user.BetRecordDo;
import com.wegame.capi.client.domain.request.BetSummaryRequest;
import com.wegame.capi.client.domain.vo.BetSummaryVo;
import com.wegame.capi.dao.mapper.user.BetRecordMapper;
import com.wegame.capi.dao.mapper.domain.user.BetRecordPo;
import com.wegame.capi.dao.mapper.domain.user.BetRecordQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * po服务
 * @author genereate
 */
@Service
public class BetRecordService {

    @Autowired
    BetRecordMapper betRecordMapper;
    @Autowired
    SingleWalletApi singleWalletApi;

    /**
     * 新增
     *
     * @param betRecordDo 投注记录
     * @return 主键id
     */
    public Long add(BetRecordDo betRecordDo) {
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);
        BetRecordPo po = BetRecordConverter.doToPo(betRecordDo, OptEnum.ADD);
        AssertUtil.assertNotNull(po, ErrorCode.ERR_PARAM);
        betRecordMapper.insert(po);
        return po.getId();
    }

    /**
     * 批量新增
     *
     * @param doList 游戏记录列表
     * @return true/false
     */
    public boolean batchAdd(List<BetRecordDo> doList) {
        AssertUtil.assertNotEmpty(doList, ErrorCode.ERR_PARAM);
        betRecordMapper.batchInsert(BetRecordConverter.doToPoList(doList, OptEnum.ADD));
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
        betRecordMapper.deleteById(id);
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
        betRecordMapper.deleteByIds(ids);
        return true;
    }

    /**
     * 修改
     *
     * @param betRecordDo 投注记录
     * @return true/false
     */
    public boolean update(BetRecordDo betRecordDo) {
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betRecordDo.getId(), ErrorCode.ERR_PARAM);
        betRecordMapper.updateById(BetRecordConverter.doToPo(betRecordDo, OptEnum.UPDATE));
        return true;
    }

    /**
     * 查询：根据主键id查询
     *
     * @param id 主键id
     * @return BetRecordDo 投注记录
     */
    public BetRecordDo get(Long id) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        return BetRecordConverter.poToDo(betRecordMapper.selectById(id));
    }

    /**
     * 查询：根据条件查询
     *
     * @param query 查询条件
     * @return BetRecordDo 投注记录
     */
    public BetRecordDo selectOne(BetRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return BetRecordConverter.poToDo(betRecordMapper.selectOne(query));
    }

    /**
     * 查询Id：根据条件查询
     *
     * @param query 查询条件
     * @return id
     */
     public Long selectId(BetRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return betRecordMapper.selectId(query);
     }

    /**
     * 查询：根据id列表
     *
     * @param ids id列表
     * @return List<BetRecordDo> 游戏记录列表
     */
    public List<BetRecordDo> listByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return BetRecordConverter.poToDoList(betRecordMapper.listByIds(ids));
    }

    /**
     * 查询全部
     *
     * @return List<BetRecordDo> 游戏记录列表
     */
    public List<BetRecordDo> listAll() {
        return BetRecordConverter.poToDoList(betRecordMapper.listAll());
    }

    /**
     * 查询id列表
     *
     * @param query 查询参数
     * @return List<Long> id列表
     */
    public List<Long> listId(BetRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return betRecordMapper.listId(query);
    }

    /**
     * 查询列表
     *
     * @param query 查询参数
     * @return List<BetRecordDo> 游戏记录列表
     */
    public List<BetRecordDo> list(BetRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return BetRecordConverter.poToDoList(betRecordMapper.list(query));
    }

    /**
     * 数量统计
     *
     * @param query 查询参数
     * @return 数量
     */
    public long count(BetRecordQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return betRecordMapper.count(query);
    }

    ///////////////  above auto generated ///////////////

    public BetSummaryVo betSummary(BetSummaryRequest request) {
        String hash = singleWalletApi.hash(request.getChannelId(), request.getHash(), request.getRefId(), DateUtil.toUTC(request.getCreateTime()));
        AssertUtil.assertTrue(StringUtils.equals(hash, request.getHash()), ErrorCode.ERR_PARAM);
        // todo 麦冬 统计结果
        return BetSummaryVo.builder().build();
    }
}