//package com.wegame.capi.dao.repository.domain.jackpot;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.NoArgsConstructor;
//import lombok.experimental.SuperBuilder;
//import org.springframework.data.domain.Sort;
//
//import java.io.Serializable;
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * 用户jackpot中奖记录查询模型
// * @author genereate
// */
//@Data
//@SuperBuilder
//@NoArgsConstructor
//@AllArgsConstructor
//public class JackpotUserRewardRecordTypesQuery implements Serializable {
//    private static final long serialVersionUID = -1380358365298628608L;
//    /**
//     * limit
//     */
//    private Integer limit;
//    /**
//     * offset
//     */
//    private Integer offset;
//    /**
//     * 排序语句
//     */
//    private String orderByClause;
//
//    ///////////////  above auto generated ///////////////
//    /**
//     * 排序语句
//     */
//    private Sort.Direction direction;
//    /**
//     * 奖池类型列表
//     */
//    private List<Integer> rewardPoolTypeList;
//    /**
//     * 小于最后一条的Id
//     */
//    private Long ltLastId;
//    /**
//     * 大于最后一条的Id
//     */
//    private Long gtLastId;
//
//    public String getCacheKey() {
//        return "types:" +
//                rewardPoolTypeList.stream().map(String::valueOf).collect(Collectors.joining("-")) +
//                ":limit:" + limit +
//                ":offset:" + offset;
//    }
//}
//
