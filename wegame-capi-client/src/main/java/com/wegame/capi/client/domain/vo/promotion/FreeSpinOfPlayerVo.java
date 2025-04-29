package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ApiModel(description = "free spin 记录")
public class FreeSpinOfPlayerVo implements Serializable {
    private static final long serialVersionUID = 889955515756526338L;
    @ApiModelProperty("playerName")
    private String playerName;
    @ApiModelProperty("refId")
    private String refId;
    @ApiModelProperty("current")
    private List<FreeSpin> current;
    @ApiModelProperty("others")
    private List<FreeSpin> others;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @ApiModel(description = "free spin 记录")
    public static class FreeSpin implements Serializable {
        private static final long serialVersionUID = 85667361882570293L;
        @ApiModelProperty("channelId")
        private String channelId;
        @ApiModelProperty("campaignId")
        private String campaignId;
        @ApiModelProperty("campaignName")
        private String campaignName;
        @ApiModelProperty("initCount")
        private Integer initCount;
        @ApiModelProperty("remainingCount")
        private Integer remainingCount;
        @ApiModelProperty("expireDate")
        private Integer expireDate;
        @ApiModelProperty("gameId")
        private String gameId;
        @ApiModelProperty("gameIdRef")
        private String gameIdRef;
        @ApiModelProperty("gameName")
        private String gameName;
        @ApiModelProperty("totalWin")
        private BigDecimal totalWin;
        @ApiModelProperty("freeBet")
        private BigDecimal freeBet;
    }
}
