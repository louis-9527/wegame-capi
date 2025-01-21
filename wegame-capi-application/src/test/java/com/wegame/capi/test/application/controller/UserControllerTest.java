package com.wegame.capi.test.application.controller;

import cn.hutool.core.lang.Assert;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.client.domain.request.UserLoginRequest;
import com.wegame.capi.test.BaseTest;
import com.wegame.capi.application.controller.UserController;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

@Slf4j
public class UserControllerTest extends BaseTest {

    @Autowired
    UserController userController;

    @BeforeAll
    public static void beforeTest() {
        UserInfoContext.setUserInfo(UserInfo.builder()
                .id("10000")
                .build());
    }

    @Test
    public void test_me() {
        CommonResponse<UserInfo> response = userController.me();
        log.info("me: {}", response);
        Assert.notNull(response);
        Assert.isNull(response.getData());
    }

    @Test
    public void test_login() {
        UserLoginRequest request = new UserLoginRequest();
        request.setPId("55");
        request.setChannelId("1051");
        request.setGameId("zhumeng001");
        request.setPlayerId("igoslot01");
        request.setPlayerName("igoslot01");
        request.setUserName("1204352556");
        request.setNickName("YUAN6401");
        request.setUserLevel("1");
        request.setToken("13820fff6c479bfb53107bc8c43b81c6be7881b21dca68e8db6b9f2265599f968b5c8509eb065535ae1395483a741b96658fc228a1da41d4620d1765daa2588c59b86d0310e3e434ab252a7b4a58e265b61913194d845f17a6afec2867b8e964");
        HttpServletResponse response = new HttpServletResponse() {
            @Override
            public void addCookie(Cookie cookie) {

            }

            @Override
            public boolean containsHeader(String s) {
                return false;
            }

            @Override
            public String encodeURL(String s) {
                return null;
            }

            @Override
            public String encodeRedirectURL(String s) {
                return null;
            }

            @Override
            public String encodeUrl(String s) {
                return null;
            }

            @Override
            public String encodeRedirectUrl(String s) {
                return null;
            }

            @Override
            public void sendError(int i, String s) throws IOException {

            }

            @Override
            public void sendError(int i) throws IOException {

            }

            @Override
            public void sendRedirect(String s) throws IOException {

            }

            @Override
            public void setDateHeader(String s, long l) {

            }

            @Override
            public void addDateHeader(String s, long l) {

            }

            @Override
            public void setHeader(String s, String s1) {

            }

            @Override
            public void addHeader(String s, String s1) {

            }

            @Override
            public void setIntHeader(String s, int i) {

            }

            @Override
            public void addIntHeader(String s, int i) {

            }

            @Override
            public void setStatus(int i) {

            }

            @Override
            public void setStatus(int i, String s) {

            }

            @Override
            public int getStatus() {
                return 0;
            }

            @Override
            public String getHeader(String s) {
                return null;
            }

            @Override
            public Collection<String> getHeaders(String s) {
                return null;
            }

            @Override
            public Collection<String> getHeaderNames() {
                return null;
            }

            @Override
            public String getCharacterEncoding() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public ServletOutputStream getOutputStream() throws IOException {
                return null;
            }

            @Override
            public PrintWriter getWriter() throws IOException {
                return null;
            }

            @Override
            public void setCharacterEncoding(String s) {

            }

            @Override
            public void setContentLength(int i) {

            }

            @Override
            public void setContentLengthLong(long l) {

            }

            @Override
            public void setContentType(String s) {

            }

            @Override
            public void setBufferSize(int i) {

            }

            @Override
            public int getBufferSize() {
                return 0;
            }

            @Override
            public void flushBuffer() throws IOException {

            }

            @Override
            public void resetBuffer() {

            }

            @Override
            public boolean isCommitted() {
                return false;
            }

            @Override
            public void reset() {

            }

            @Override
            public void setLocale(Locale locale) {

            }

            @Override
            public Locale getLocale() {
                return null;
            }
        };
        CommonResponse<UserInfo> result = userController.login(response, request);
        Assert.notNull(response);
        Assert.isNull(result.getData());
    }

    @Test
    public void test_getCredit() {

    }

}
