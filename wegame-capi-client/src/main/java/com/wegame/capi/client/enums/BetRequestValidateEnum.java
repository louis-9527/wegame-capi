package com.wegame.capi.client.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.validator.*;
import lombok.Getter;
import lombok.ToString;

/**
 * 游戏类型枚举
 *
 * @author apple
 */
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum BetRequestValidateEnum {

    MARIA_MAKILING(GameTypeEnum.MARIA_MAKILING, (betType, betSubType, betDetail) -> {
        MariaMakilingValidator.validateParam(betType, betSubType, betDetail);
        CommonGameValidator.validateStarCardBet(betType, betSubType, betDetail);
        CommonGameValidator.validateFreeSpinBet(betType, betSubType, betDetail);
    }),
    MALAKAS(GameTypeEnum.MALAKAS, (betType, betSubType, betDetail) -> {
        MalakasValidator.validateParam(betType, betSubType, betDetail);
        CommonGameValidator.validateStarCardBet(betType, betSubType, betDetail);
        CommonGameValidator.validateFreeSpinBet(betType, betSubType, betDetail);
    }),
    IBONG_ADARNA(GameTypeEnum.IBONG_ADARNA, (betType, betSubType, betDetail) -> {
        IbongAdarnaValidator.validateParam(betType, betSubType, betDetail);
        CommonGameValidator.validateFreeSpinBet(betType, betSubType, betDetail);
    }),
    LAM_ANG(GameTypeEnum.LAM_ANG, (betType, betSubType, betDetail) -> {
        LamangValidator.validateParam(betType, betSubType, betDetail);
        CommonGameValidator.validateFreeSpinBet(betType, betSubType, betDetail);
        CommonGameValidator.validateStarCardBet(betType, betSubType, betDetail);
    }),
    BAKUNAWA(GameTypeEnum.BAKUNAWA, (betType, betSubType, betDetail) -> {
        BakunawaValidator.validateParam(betType, betSubType, betDetail);
        CommonGameValidator.validateFreeSpinBet(betType, betSubType, betDetail);
    }),
    DIWATA_MAYARI(GameTypeEnum.DIWATA_MAYARI, (betType, betSubType, betDetail) -> {
        DiwataMayariValidator.validateParam(betType, betSubType, betDetail);
        CommonGameValidator.validateFreeSpinBet(betType, betSubType, betDetail);
        CommonGameValidator.validateStarCardBet(betType, betSubType, betDetail);
    }),
    KAPRE(GameTypeEnum.KAPRE, (betType, betSubType, betDetail) -> {
        KapreValidator.validateParam(betType, betSubType, betDetail);
        CommonGameValidator.validateFreeSpinBet(betType, betSubType, betDetail);
        CommonGameValidator.validateStarCardBet(betType, betSubType, betDetail);
    }),
    MANGKUKULAM(GameTypeEnum.MANGKUKULAM, (betType, betSubType, betDetail) -> {
        MangkukulamValidator.validateParam(betType, betSubType, betDetail);
        MangkukulamValidator.validateStarCardBet(betType, betSubType, betDetail);
        CommonGameValidator.validateFreeSpinBet(betType, betSubType, betDetail);
    }),
    SIRENA(GameTypeEnum.SIRENA, (betType, betSubType, betDetail) -> {
        SirenaValidator.validateParam(betType, betSubType, betDetail);
        // SirenaValidator.validateStarCardBet(betType, betSubType, betDetail); todo @lyric
        // SirenaValidator.validateFreeSpinBet(betType, betSubType, betDetail); todo @lyric
    }),
    ATLANTIS(GameTypeEnum.ATLANTIS, (betType, betSubType, betDetail) -> {
        AtlantisValidator.validateParam(betType, betSubType, betDetail);
        AtlantisValidator.validateStarCardBet(betType, betSubType, betDetail);
        CommonGameValidator.validateFreeSpinBet(betType, betSubType, betDetail);
    }),





    ;
    private final GameTypeEnum gameType;

    private final BetRequestValidator validator;

    BetRequestValidateEnum(GameTypeEnum gameType, BetRequestValidator validator) {
        this.gameType = gameType;
        this.validator = validator;
    }

    public static BetRequestValidateEnum valueOfGameType(GameTypeEnum gameType) {
        for (BetRequestValidateEnum item : BetRequestValidateEnum.values()) {
            if (item.getGameType() == gameType) {
                return item;
            }
        }
        return null;
    }

    public void validate(BetTypeEnum betType, BetSubTypeEnum betSubType, BetRequest.BetDetail detail) {
        validator.validate(betType, betSubType, detail);
    }
}
