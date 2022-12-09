package com.lin.simplecron.service;

import cn.hutool.core.util.StrUtil;
import com.lin.simplecron.config.Constants;
import com.lin.simplecron.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * com.lin.simplecron.service
 *
 * @author quanlinlin
 * @date 2022/10/19 19:23
 * @since
 */
@Service
@Slf4j
public class LoginTokenService {

    public static final String LOGIN_TOKEN_CACHE_KEY = RedisUtil.getStandardStringKeyName(Constants.CacheConsts.JIEMO_LOGIN_TOKEN);

    // 当 loginToken 还剩不足一天的时候,开始 im 通知更新
    public static final Integer LOGIN_TOKEN_ALERT_HOUR = 24;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ImService imService;

    public String getCurrentLoginToken() {
        return redisTemplate.opsForValue().get(LOGIN_TOKEN_CACHE_KEY);
    }


    public String updateLoginToken(String newToken) {
        String oldToken = redisTemplate.opsForValue().get(LOGIN_TOKEN_CACHE_KEY);
        log.info("Updated login token, old token: {}, new token: {}", oldToken, newToken);
        redisTemplate.opsForValue().set(LOGIN_TOKEN_CACHE_KEY, newToken, Duration.ofDays(20));
        return oldToken;
    }

    public void checkLoginTokenExpired() {
        Long expire = redisTemplate.getExpire(LOGIN_TOKEN_CACHE_KEY, TimeUnit.HOURS);
        if (Objects.isNull(expire) || expire < LOGIN_TOKEN_ALERT_HOUR) {
            // 没有新值,先用旧值将就用吧,保持im提醒
            String oldToken = redisTemplate.opsForValue().get(LOGIN_TOKEN_CACHE_KEY);
            if (oldToken != null) {
                redisTemplate.opsForValue().set(LOGIN_TOKEN_CACHE_KEY, oldToken, Duration.ofDays(1));
            }

            imService.sendImMsg(StrUtil.format("芥末圈 loginToken 即将过期,当前剩余时间: {} 小时", expire));
        }
        log.info("芥末圈 loginToken 当前剩余时间: {} 小时", expire);
    }
}
