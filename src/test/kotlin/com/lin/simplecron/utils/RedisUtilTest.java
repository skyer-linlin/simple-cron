package com.lin.simplecron.utils;

import org.junit.jupiter.api.Test;

import static com.lin.simplecron.config.Constants.CacheConsts.JIEMO_LOGIN_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * com.lin.simplecron.utils
 *
 * @author quanlinlin
 * @date 2022/11/19 02:48
 * @since
 */
class RedisUtilTest {


    @Test
    void testGetStandardKeyName() {
    }

    @Test
    void getStandardStringKeyName() {
        assertEquals("simplecron:jiemo.logintoken:string", RedisUtil.getStandardStringKeyName(JIEMO_LOGIN_TOKEN));

    }
}