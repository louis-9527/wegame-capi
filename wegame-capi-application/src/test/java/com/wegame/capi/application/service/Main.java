package com.wegame.capi.application.service;

import com.wegame.base.utils.FastJsonUtils;

public class Main {

    public static void main(String[] args) {
        int[][] arr = {{1,2, 3},{3,4},{5,6}};

        System.out.println(FastJsonUtils.toJSONString(arr));

    }
}
