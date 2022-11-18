package com.lin.simplecron.utils;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;

import static com.lin.simplecron.config.Constants.CacheConsts;
import static com.lin.simplecron.config.Constants.RedisValueType;

/**
 * com.lin.simplecron.utils
 *
 * @author quanlinlin
 * @date 2022/11/19 02:33
 * @since
 */
@UtilityClass
public class RedisUtil {
    public static String getStandardKeyName(String key, RedisValueType valueType) {
        return StrUtil.join(":", CacheConsts.KEY_PREFIX, key, valueType.getName());
    }

    public static String getStandardStringKeyName(String key) {
        return getStandardKeyName(key, RedisValueType.STRING);
    }
}
