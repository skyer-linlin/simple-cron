package com.lin.simplecron.config;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * com.lin.followtrade.config
 *
 * @author quanlinlin
 * @date 2021/4/29 22:24
 * @since
 */
@Configuration
public class ImConfig {

    @Bean("notifyBot")
    public TelegramBot myNotifyBot() {
        final Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("192.168.31.32", 7890));
        final OkHttpClient okHttpClient = new OkHttpClient.Builder().proxy(proxy).build();
        final TelegramBot telegramBot = new TelegramBot.Builder("1761282109:AAH-yp8zi4mOZwYCZP0VUXpc_8fy9Ea6nV8")
            .okHttpClient(okHttpClient)
            .build();

        telegramBot.setUpdatesListener(
            updates -> {
                // ... process updates
                // return id of last processed update or confirm them all
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        );
        return telegramBot;
    }
}
