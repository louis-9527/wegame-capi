package com.wegame.capi.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Objects;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GameRewardAmountTypeEnum {

        NONE_PRIZE(-1, "未中大奖"),
        NICE_PRIZE(0, "nice"),
        MEGE_PRIZE(1, "mege"),
        SUPER_PRIZE(2, "super"),
        SENSATIONAL_PRIZE(3, "sensational");


        /**
         * 枚举码.
         */
        private final Integer code;

        /**
         * 描述信息.
         */
        private final String desc;

        /**
         * 私有构造方法.
         *
         * @param code code值
         * @param desc 描述信息
         */
        private GameRewardAmountTypeEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public static GameRewardAmountTypeEnum valueOfCode(Integer code) {
            for (GameRewardAmountTypeEnum item : GameRewardAmountTypeEnum.values()) {
                if (Objects.equals(item.getCode(), code)) {
                    return item;
                }
            }
            return null;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

}
