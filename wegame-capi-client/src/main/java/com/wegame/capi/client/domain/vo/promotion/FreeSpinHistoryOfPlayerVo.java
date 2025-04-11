package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ApiModel(description = "free spin 历史记录")
public class FreeSpinHistoryOfPlayerVo implements Serializable {
    private static final long serialVersionUID = -8527322307778058829L;
    @ApiModelProperty("playerName")
    private String playerName;
    @ApiModelProperty("refId")
    private String refId;
    @ApiModelProperty("history")
    private List<FreeSpin> freeSpin;
    @ApiModelProperty("totalPages")
    private Integer totalPages;
    @ApiModelProperty("page")
    private Integer page;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @ApiModel(description = "free spin 历史记录item")
    public static class FreeSpin implements Serializable {
        private static final long serialVersionUID = -8735610005629937885L;
        @ApiModelProperty("campaignId")
        private String campaignId;
        @ApiModelProperty("initCount")
        private Integer initCount;
        @ApiModelProperty("remainingCount")
        private Integer remainingCount;
        @ApiModelProperty("gameId")
        private String gameId;
        @ApiModelProperty("gameName")
        private String gameName;
        @ApiModelProperty("totalWin")
        private Integer totalWin;
        @ApiModelProperty("firstUsedTime")
        private Integer firstUsedTime;
    }
}
