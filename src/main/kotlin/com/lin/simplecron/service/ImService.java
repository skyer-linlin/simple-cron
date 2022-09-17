package com.lin.simplecron.service;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * com.lin.followtrade.service
 *
 * @author quanlinlin
 * @date 2021/4/29 17:22
 * @since
 */
@Service
@Slf4j
public class ImService {

    @Autowired
    @Qualifier("notifyBot")
    private TelegramBot telegramBot;

    public void sendImMsg(String msg) {
        sendMsg2Chat("-1001348542716", msg);
    }

    public void sendMsg2Chat(String chatId, String msg) {
        final var sendMessage = new SendMessage(chatId, msg);
        // 异步发送 im 消息
        telegramBot.execute(
            sendMessage,
            new Callback<SendMessage, SendResponse>() {
                @Override
                public void onResponse(SendMessage request, SendResponse response) {
                    log.info("sending telegram msg success, response:{}", response);
                }

                @Override
                public void onFailure(SendMessage request, IOException e) {
                    log.error("sending telegram msg fail, exception:", e);
                }
            }
        );
    }
}
