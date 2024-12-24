package com.wegame.capi.api.domain.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BasePageRes extends BaseRes {
    private Integer totalResult;
    private Integer limit;
    private Integer page;
    private Integer totalPages;
    private Integer pagingCounter;
    private Boolean hasPrevPage;
    private Boolean hasNextPage;
    private Integer offset;
    private Object prevPage;
    private Object nextPage;
    private Double totalReceipt;
    private Double totalBet;
}
