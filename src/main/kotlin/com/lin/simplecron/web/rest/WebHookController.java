package com.lin.simplecron.web.rest;

import cn.hutool.core.util.StrUtil;
import com.lin.simplecron.service.ImService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * com.lin.simplecron.web.rest
 *
 * @author quanlinlin
 * @date 2022/11/7 05:13
 * @since
 */
@RestController
@Slf4j
@RequestMapping("/api/webhook")
public class WebHookController {
    @Autowired
    private ImService imService;

    @PostMapping("/notify/myTelegramBot")
    public ResponseEntity<String> notifyLarrySpeakGroup(@RequestBody String msg) {
        if (StrUtil.isNotBlank(msg)) {
            msg = msg.trim();
            log.info("webhook 发送消息至 telegram: {}", StrUtil.subPre(msg, 10));
            imService.sendImMsg(msg);
            return ResponseEntity.ok("sucess");
        } else {
            log.info("webhook 验证消息不通过, msg:{}", msg);
            return ResponseEntity.badRequest().body("error msg:" + msg);
        }
    }
}
