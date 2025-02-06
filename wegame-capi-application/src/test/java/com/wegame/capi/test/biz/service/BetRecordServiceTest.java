//package com.wegame.capi.test.biz.service;
//
//import cn.hutool.core.collection.CollectionUtil;
//import com.wegame.base.context.OperatorContext;
//import com.wegame.base.id.IdGeneratorHelper;
//import com.wegame.base.utils.FastJsonUtils;
//import com.wegame.base.utils.OperatorUtil;
//import com.wegame.capi.biz.converter.BetRecordConverter;
//import com.wegame.capi.biz.domain.biz.BetRecordBatchResultDo;
//import com.wegame.capi.biz.domain.biz.BetRecordDo;
//import com.wegame.capi.biz.service.BetRecordService;
//import com.wegame.capi.client.enums.GameTypeEnum;
//import com.wegame.capi.common.enums.BetStatusEnum;
//import com.wegame.capi.dao.mapper.domain.business.BetRecordQuery;
//import com.wegame.capi.test.BaseTest;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//import java.util.Optional;
//
//@Slf4j
//public class BetRecordServiceTest extends BaseTest {
//    @Autowired
//    BetRecordService betRecordService;
//    @Autowired
//    IdGeneratorHelper idGeneratorHelper;
//
//    @Before
//    public void init() {
//        //
//
//
//        log.info("init");
//    }
//
//    @Test
//    public void test_batchAdd_allSuc() {
//        OperatorContext.setOperator(OperatorUtil.getOperator());
//        List<BetRecordDo> betRecordDoList = this.initBatchAddData();
//        betRecordDoList.forEach(betRecordDo -> {
//            betRecordDo.setId(idGeneratorHelper.nextId());
//            betRecordDo.setStatus(BetStatusEnum.ING.getCode());
//        });
//
//        BetRecordBatchResultDo betRecordBatchResultDo = betRecordService.batchAdd(betRecordDoList, GameTypeEnum.MARIA_MAKILING);
//        log.info("test_batchAdd result:{}", FastJsonUtils.toJSONString(betRecordBatchResultDo));
//        Assert.assertNotNull(betRecordBatchResultDo);
//        Assert.assertTrue(CollectionUtil.isEmpty(betRecordBatchResultDo.getNormalFailIdList()));
//    }
//
//    @Test
//    public void test_batchAdd_partSuc() {
//        OperatorContext.setOperator(OperatorUtil.getOperator());
//        List<BetRecordDo> betRecordDoList = this.initBatchAddData();
//        // 一半更新，一半插入失败
//        for (int i = 0; i < betRecordDoList.size(); i += 2) {
//            betRecordDoList.get(i).setId(idGeneratorHelper.nextId());
//            betRecordDoList.get(i).setStatus(BetStatusEnum.ING.getCode());
//        }
//
//        BetRecordBatchResultDo betRecordBatchResultDo = betRecordService.batchAdd(betRecordDoList, GameTypeEnum.MARIA_MAKILING);
//        log.info("test_batchAdd_partSuc result:{}", FastJsonUtils.toJSONString(betRecordBatchResultDo));
//        Assert.assertNotNull(betRecordBatchResultDo);
//        Assert.assertTrue(CollectionUtil.isNotEmpty(betRecordBatchResultDo.getDupKeyIdList()));
//    }
//
//    @Test
//    public void test_batchUpdate_allSuc() {
//        OperatorContext.setOperator(OperatorUtil.getOperator());
//        List<BetRecordDo> betRecordDoList = this.initBatchUpdateData();
//        betRecordDoList.forEach(betRecordDo -> {
//            betRecordDo.setStatus(BetStatusEnum.SUCCESS.getCode());
//        });
//        BetRecordBatchResultDo betRecordBatchResultDo = betRecordService.batchUpdate(betRecordDoList, GameTypeEnum.MARIA_MAKILING);
//        Assert.assertNotNull(betRecordBatchResultDo);
//        Assert.assertTrue(CollectionUtil.isEmpty(betRecordBatchResultDo.getNormalFailIdList()));
//    }
//
//    @Test
//    public void test_batchUpdate_partSuc() {
//        OperatorContext.setOperator(OperatorUtil.getOperator());
//        List<BetRecordDo> betRecordDoList = this.initBatchUpdateData();
//        betRecordDoList.forEach(betRecordDo -> {
//            betRecordDo.setStatus(BetStatusEnum.SUCCESS.getCode());
//        });
//        // 第一条和最后一条出现一个不存在的id，进行更新
//        betRecordDoList.get(0).setId(idGeneratorHelper.nextId());
//        betRecordDoList.get(betRecordDoList.size() - 1).setId(idGeneratorHelper.nextId());
//        BetRecordBatchResultDo betRecordBatchResultDo = betRecordService.batchUpdate(betRecordDoList, GameTypeEnum.MARIA_MAKILING);
//        Assert.assertNotNull(betRecordBatchResultDo);
//        Assert.assertTrue(CollectionUtil.isNotEmpty(betRecordBatchResultDo.getNormalFailIdList()));
//    }
//
//    private List<BetRecordDo> initBatchAddData() {
//        // 从数据库获取数据
//        /*
//        {
//"endCreateTime": 1987013738755,
//"pageNo": 1,
//"pageSize": 20,
//"startCreateTime": 1737013738755,
//"userId": "1",
//"lastCreateTime":null
//
//}
//         */
//        BetRecordQuery query = new BetRecordQuery();
////        query.setId(Optional.ofNullable(request.getId()).map(Long::valueOf).orElse(null));
//        query.setPlayerId("igoslot01");
//        query.setStartCreateTime(1737013738755L);
//        query.setEndCreateTime(1987013738755L);
//        query.setOrderByClause("createTime");
//        query.setLimit(10);
//        query.setLastId(null);
//        return betRecordService.listForPage(query, GameTypeEnum.MARIA_MAKILING);
//    }
//
//    private List<BetRecordDo> initBatchUpdateData() {
//        BetRecordQuery query = new BetRecordQuery();
////        query.setId(Optional.ofNullable(request.getId()).map(Long::valueOf).orElse(null));
//        query.setPlayerId("igoslot01");
//        query.setStartCreateTime(1737013738755L);
//        query.setEndCreateTime(1987013738755L);
//        query.setOrderByClause("createTime");
//        query.setStatus(BetStatusEnum.ING.getCode());
//        query.setLimit(10);
//        query.setLastId(null);
//        return betRecordService.listForPage(query, GameTypeEnum.MARIA_MAKILING);
//    }
//}
