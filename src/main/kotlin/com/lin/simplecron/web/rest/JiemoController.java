package com.lin.simplecron.web.rest;

import com.lin.simplecron.service.TopicService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * com.lin.simplecron.controller
 *
 * @author quanlinlin
 * @date 2022/9/14 18:03
 * @since
 */
@Controller
@Tag(name = "主题页面", description = "topic 相关页面提供数据")
public class JiemoController {
    private final TopicService topicService;

    @Value(value = "${server.host}")
    private String host;

    @Value(value = "${server.port}")
    private Integer port;

    public JiemoController(TopicService topicService) {
        this.topicService = topicService;
    }

    @RequestMapping("/jiemo")
    @Timed(value = "main_page_request_duration", description = "Time taken to return main page", histogram = true)
    public String jiemo(Model model) {
        model.addAttribute("topics", topicService.findAll());
        model.addAttribute("uriPrefix", host + ":" + port);
        model.addAttribute("host", host);
        return "jiemo";
    }
}
