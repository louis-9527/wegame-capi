package com.wegame.capi.biz.service.backend;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.backend.GameMaintainConfigConverter;
import com.wegame.capi.biz.domain.backend.GameMaintainConfigDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.backend.GameMaintainConfigMapper;
import com.wegame.capi.dao.mapper.domain.backend.GameMaintainConfigPo;
import com.wegame.capi.dao.mapper.domain.backend.GameMaintainConfigQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * po服务
 * @author genereate
 */
@Service
public class GameMaintainConfigService {

    @Autowired
    GameMaintainConfigMapper gameMaintainConfigMapper;

    /**
     * 查询：根据条件查询
     *
     * @param query 查询条件
     * @return GameMaintainConfigDo 游戏维护配置
     */
    public GameMaintainConfigDo selectOne(GameMaintainConfigQuery query) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return GameMaintainConfigConverter.poToDo(gameMaintainConfigMapper.selectOne(query));
    }



    ///////////////  above auto generated ///////////////

    @Cacheable(value = CacheConstants.GAME_MAINTAIN_CONFIG, key = "#gameId")
    public GameMaintainConfigDo selectByGameId(String gameId) {
        AssertUtil.assertNotNull(gameId, ErrorCode.ERR_PARAM);
        return GameMaintainConfigConverter.poToDo(gameMaintainConfigMapper.selectOne(buildQuery(gameId)));
    }

    @CacheEvict(value = CacheConstants.GAME_MAINTAIN_CONFIG, key = "#gameId")
    public void evict(String gameId) {
        // do nothing
    }

    private GameMaintainConfigQuery buildQuery(String gameId) {
        return GameMaintainConfigQuery.builder().gameId(gameId).build();
    }
}