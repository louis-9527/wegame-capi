package com.wegame.capi.test;


import com.wegame.capi.application.Application;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.wegame.capi.scheduler.*"))
public class BaseTest {

    //        {
    //            "birthday": "01-01-1990",
    //                "channelId": "1051",
    //                "nickName": "IgoTest10020",
    //                "gameId": "zhumeng002",
    //                "pId": "55",
    //                "playerId": "IgoTest10020",
    //                "playerName": "IgoTest10020",
    //                "token": "13820fff6c479bfb53107bc8c43b81c6be7881b21dca68e8db6b9f2265599f96364f19e939f86dfeda3dd7e4d115050939a8d1e83b6a5c6a76ae9d1a610f9300054725e423edbe9b3c8d7bfc5d815b2435df548520f3f1750356e22a5f6f8065",
    //                "userLevel": "1",
    //                "userName": "IgoTest10020"
    //        }
    public static final String TEST_PLAYER_ID = "IgoTest10020";
    public static final String TEST_PLAYER_NAME = "IgoTest10020";
    public static final String TEST_USER_NAME = "IgoTest10020";
    public static final String TEST_NICK_NAME = "IgoTest10020";
    public static final String TEST_USER_LEVEL = "1";
    public static final String TEST_TOKEN = "13820fff6c479bfb53107bc8c43b81c6be7881b21dca68e8db6b9f2265599f96364f19e939f86dfeda3dd7e4d115050939a8d1e83b6a5c6a76ae9d1a610f9300054725e423edbe9b3c8d7bfc5d815b2435df548520f3f1750356e22a5f6f8065";

}

