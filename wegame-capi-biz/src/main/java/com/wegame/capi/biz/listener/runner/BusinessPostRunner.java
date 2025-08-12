package com.wegame.capi.biz.listener.runner;

/**
 * 业务侧后执行runner
 */
public interface BusinessPostRunner {

    <T> T run();
}
