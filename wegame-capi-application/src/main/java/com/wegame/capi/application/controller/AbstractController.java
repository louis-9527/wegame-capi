package com.wegame.capi.application.controller;

import javax.servlet.http.HttpServletResponse;

public class AbstractController {

    public void noCacheControl(HttpServletResponse response) {
        response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Expires", "0");
        response.addHeader("Expires", "0");
    }
}
