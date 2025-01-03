package com.wegame.capi.biz.domain.biz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MysteriousBuyFreeSpinsBetResultDo {
    private List<Integer> freeSpinIds;
    private GameBetResultDetailDo resultDetailDo;
}
