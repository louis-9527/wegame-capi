package com.wegame.capi.client.context;

/**
 * UserInfoContext
 *
 * @author apple
 */
public class UserInfoContext {

    private static ThreadLocal<UserInfo> userInfoThreadLocal = new ThreadLocal<>();

    public static UserInfo getUserInfo() {
        return userInfoThreadLocal.get();
    }

    public static void setUserInfo(UserInfo info) {
        userInfoThreadLocal.set(info);
    }

    public static void clear() {
        userInfoThreadLocal.remove();
    }
}
