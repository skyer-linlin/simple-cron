package com.lin.simplecron.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    private String loginToken = "darling_829108d8_0_285071c6715ce2f9466ecf72630c0e72";

    public String getCurrentLoginToken() {
        return loginToken;
    }


    public String updateLoginToken(String newToken) {
        log.info("Updated login token, old token: {}, new token: {}", loginToken, newToken);
        String oldToken = loginToken;
        loginToken = newToken;
        return oldToken;
    }
}
