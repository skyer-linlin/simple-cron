package com.lin.simplecron.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * com.lin.simplecron.config
 *
 * @author quanlinlin
 * @date 2022/9/15 03:28
 * @since
 */
public interface Constants {

    @AllArgsConstructor
    @Getter
    enum Cex {
        BINANCE("Binance"),
        BITMEX("Bitmex"),
        OKEX("OKX"),
        BYBIT("Bybit"),
        GATE("Gate"),
        DERIBIT("Deribit"),
        BITGET("Bitget"),
        COIN_EX("CoinEx"),
        OTHER_ERR("ERR"),
        ;
        private String name;

        public static Cex nameOf(String cexName) {
            Cex cex = Arrays.stream(Cex.values())
                .filter(cexEnum -> StringUtils.equals(cexName, cexEnum.getName()))
                .findFirst().orElse(OTHER_ERR);
            return cex;
        }

    }

    @AllArgsConstructor
    @Getter
    enum RedisValueType {
        STRING("string"), HASH("hash"),
        SET("set"), ZSET("zset"), LIST("list"),
        ;
        private String name;
    }

    class CacheConsts {

        public static final String KEY_PREFIX = "simplecron";
        public static final String JIEMO_LOGIN_TOKEN = "jiemo.logintoken";
        public static final String JIEMO_GROUPS_SET = "jiemo.groups";
    }

}