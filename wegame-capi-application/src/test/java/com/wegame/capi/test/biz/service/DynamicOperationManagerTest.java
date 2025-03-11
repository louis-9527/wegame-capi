package com.wegame.capi.test.biz.service;

import com.wegame.capi.biz.manager.MariaMakilingDynamicOperationManager;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DynamicOperationManagerTest  extends BaseTest {

    @Test
    public void reportEvent(){
        long id =1895037553443196928L;
        String eventId = "7a0de795-0692-4dc5-9f08-4a383b5d311a";
         DynamicOperationEventStatusEnum eventStatusEnum = DynamicOperationEventStatusEnum.FINISH;
        GameTypeEnum gameTypeEnum = GameTypeEnum.MARIA_MAKILING;
       // mariaMakilingDynamicOperationManager.reportEvent(id,eventId,eventStatusEnum,gameTypeEnum);
    }

}
