package com.wegame.capi.client.domain.vo.mangkukulam;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Mangkukulam元素")
@EqualsAndHashCode(callSuper = false)
public class MangkukulamSymbolVo implements Serializable {
    private static final long serialVersionUID = 3447556878793626303L;
    @ApiModelProperty("元素Id")
    private String symbolId;
    @ApiModelProperty("元素金边(false否,true是)")
    private boolean symbolGoldBorder;

    @Override
    public String toString() {
        return "";
    }
}
