package com.wegame.capi.test.biz.module.kapre.play;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.symbol.KapreSymbolDo;
import com.wegame.capi.biz.domain.biz.template.kapre.KapreBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.common.utils.JsonGzipUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

@Slf4j
public class KapreEsTemplateTest {


    @Test
    public void esDataTest() {
        String esTemplateResultStr = " {\n" +
                "\t\t\"avgMultiple\": 0,\n" +
                "\t\t\"betSubType\": \"BUY_BET\",\n" +
                "\t\t\"betType\": \"BUY_BET\",\n" +
                "\t\t\"bytes\": \"H4sIAAAAAAAAAO1dTW+bQBD9L5xRtbB4Ad+qfkiR2kZqI/UQ+eDYpInk2JXtqo2i/vdCiT+ICW5c1rw33ksUr8z6MezMvDcDy+WDdzVcjm7Oxl4/VFGqY53/SUwvirTWke9dZcvP2Wg2H9d+I0x8L/u1PJtez7z+9Mdk4nvfhnfZ6oiF17/cPiY1OjY69HfGIrU9FutEB/mH3bEoqIwlKjThk2PLscpvJGEYqhxvZcxEQZT09MD3bp8799vpOPvl9ZXvfR/Os+myMEH+4edNlk28/oN3Pc+y95PhN69/PZwssu0DFvd3V7PJ6/k8t8DlgzcaLm6+/B1af3c0u7ubTVeDy/mPrETiBUrFnu9Nczvmn8ovrMayxXJ1RGntxWi4XGbzJ3OXv34+Hm9hubj/Xsz35vzjx/NP+Vw/byfjymG//X/AuTrRAqhWKtgCWiL5F5TlyTaA/PLm9cXFu88Ho6xas1djzR6ENas4TQ1OA4hT1+DU1nEO/Jf7UVCDNAC0aFSDM4LAyejvYY01QwhrIvj7IX7kIuhp4kxrcKaAOLuJn7QZqYTUADNQuzDLsQNgvtIC2YjU3NnN+hwMfhdYa5XoSjO2qkQf1V9FYabKqDTo6ZoxsztmVM1Y9dgoUToyFSWaJmkaxybYHYuDGiW6Pvc6JVorWWu06eMFbkma4pIrRndjSbCOsLRNBDgkX6TCLSrw9j7/53b0NZ+2HaRfzz68fYKzsJLgBYqrpKqXfTt82r/ekjUpbojnSJgcKBGCu2QvYonvEPpeDI0/eh7ap0Mj1YIOfU7oFZPjCD2WyACRX49aUQteKYHV1JfjTGpwJpBZlsWiEFmBx5eOijS07PXdXPtDvAlilXJoFVEr1EVRkd1IOUVIjlbksUUVS5G0Qfytb0i1If42k+OIP4gUy0MEBcUGklDL0+jrRqWyWJRF9Tt7du9JuE9FsIR6FvbPaU+enMSipFmuPEtkYunpdNQ/3ycAo8CiACwmxxGALP12iEjGcz89D1LYIoUs5kop/iGqU64B9J81NFmtP06cLs3LSJ6SC5O4yZNFrHJ1/zraXqJB/K22mbEi/taT44g/ltI1rscBZi9BrKWjh5QF51hcnFy5i8XTebZwYonxLCwVN7dzeTpP8ZElq+PWIiiLpLiOjpAy98u9NnaceVbuQW3pwtIV5iCpp1f2b7/PBxBwdzItuUk5WTVLEsMNTQg4Jd+ihstaOZM8y3XnqaCwxCYWncqCk6Ui1Q3OBgG4fl2EDQG4mRxHALJECJZcyxIhWNg1z4MVLJ7EwgZZViiLnubpWrBYlGWF4kYmgGJft9XTnnMlcFeqLlH01jTEc73IDx40SL/HtwLakX7ryY8k/YDjGAt1aT060HjJkUOYa6E6ZcpUtWSpRbl+yql6EgtOIbSfh3q522ZxA5KtgomjnZKSOoTCpw6ZnS3K/cUHO7vMbibfV3xYv3TSbTIki6F0XpjETakIOGVtLMTZb8aNSFyezkPzccvKnDkIoX97Ogza9kMvLGUm5HILZxICbn+w0I8GqZcqo9LAUp95M/mRpB6ycmdFSk0JOHHykFaIpp6onMsiUFkEP88b0FmKUhA8m4W9CspK3diT1pNEZSUWX8KNTQDFqf0y0NiUgQap48dSdiOpYrKwVpYiO8/2KLgRF0EHyF2huAEUITId4kksHItF+bvIJEEDSPYklhUKYU/XB/x/X9onAI2yKACLyXEEIIvvsfAsFt6Kq1QBhD/tnZ+unX70JQrxbDSswx/iSbihCZAOCsLJkjp53nbFIqldpG8z0kMwEVjpsV/82bwJ1Ai9CVTmq+XlFjEhbg2Bve6Scy0uTo5cy4ESQQFIbqOzaFRcZs0YlYjKkSz1cpYFyuLwuI6EEOibxF+UKB0ZO2+Z3EwuUfzZbgSwiBUI3+OR/cBrVPLdNrgZF05akT+7UqWubucKQSuTJRsJipsdvRZP2GuPAeQ0S8dXrst3tJ9fg/hL0jSOTWBH/K0nx7ntk6Wcget5hwQyGv1jL9oinzSJlOqWEgTKslFZWv0sOHFJwYv0KQ8LtiiikU/bcRBX1oUOSbQlKfIyJEIZhWWNSkqbiK/0KLV4bFPox1BCn6W6CuFuPEmWByl+Z0pUej09wmK7yIOr80/Xi7QrQqHjpGz08TxuAhHnBck9Fp4MYU9g9llov8EfZoW+sUXzAAA=\",\n" +
                "\t\t\"gameId\": \"zhumeng007\",\n" +
                "\t\t\"hash\": \"1170577337\",\n" +
                "\t\t\"id\": 2049373493865443328,\n" +
                "\t\t\"multiple\": 1.305714,\n" +
                "\t\t\"nextBetRecordId\": 0,\n" +
                "\t\t\"tag\": \"\",\n" +
                "\t\t\"templateType\": \"COMMON\"\n" +
                "\t}";
        // result
        KapreBetRecordTemplateEsDo templateEsDo = FastJsonUtils.fromJSONString(esTemplateResultStr, KapreBetRecordTemplateEsDo.class);
        Assertions.assertNotNull(templateEsDo);

        // bytes
        List<GameRecordTemplateDo> templateList = FastJsonUtils.fromJSONString(JsonGzipUtil.decompress(templateEsDo.getBytes()), new TypeReference<List<GameRecordTemplateDo>>() {
        });
        Assertions.assertTrue(CollectionUtil.isNotEmpty(templateList));

        // wheel
        for (GameRecordTemplateDo templateDo : templateList) {
            WheelDo<KapreSymbolDo> kapreSymbolDoWheelDo = this.convertWheel(templateDo.getWheel());
            Assertions.assertNotNull(kapreSymbolDoWheelDo);

            for (KapreSymbolDo[] kapreSymbolDos : kapreSymbolDoWheelDo.getSymbolArr()) {
                for (KapreSymbolDo kapreSymbolDo : kapreSymbolDos) {
                    Assertions.assertNotNull(kapreSymbolDo);
                    if (Objects.nonNull(kapreSymbolDo.getNestSymbol())) {
                        log.info("nestSymbol:{}", kapreSymbolDo.getNestSymbol());
                    }
                }
            }
        }
    }


    private WheelDo<KapreSymbolDo> convertWheel(Object obj) {
        WheelDo<KapreSymbolDo> wheel = JSON.parseObject(JSON.toJSONString(obj), new TypeReference<WheelDo<KapreSymbolDo>>() {
        });
        AssertUtil.assertNotNull(wheel, ErrorCode.ERR_INTER);
        return wheel;
    }


}
