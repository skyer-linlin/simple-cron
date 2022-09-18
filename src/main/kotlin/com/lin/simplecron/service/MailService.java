package com.lin.simplecron.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

// @Component
@Slf4j
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailProperties mailProperties;

    @Autowired
    private Environment environment;

    public void notice(String subject, String content) {
        log.info("enter time: {}", LocalDateTime.now());

        SimpleMailMessage message = new SimpleMailMessage();
        // 收信人
        message.setTo("linlink22@gmail.com");
        // 主题
        message.setSubject(Arrays.toString(environment.getActiveProfiles()) + " : " + subject);
        // 内容
        message.setText(content);
        // 发信人
        message.setFrom(mailProperties.getUsername());

        mailSender.send(message);
        log.info("发送邮件至联系人, 标题: {}", subject);
    }
}
