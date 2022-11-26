package com.lin.simplecron.web.rest;

import cn.hutool.core.util.StrUtil;
import com.lin.simplecron.service.GroupService;
import com.lin.simplecron.service.TopicService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

/**
 * com.lin.simplecron.controller
 *
 * @author quanlinlin
 * @date 2022/9/14 18:03
 * @since
 */
@Controller
@Tag(name = "主题页面", description = "topic 相关页面提供数据")
@RequestMapping("/page/jiemo/")
public class JiemoController {
    private final TopicService topicService;
    private final GroupService groupService;

    @Value(value = "${server.host}")
    private String host;

    @Value(value = "${server.port}")
    private Integer port;

    public JiemoController(TopicService topicService, GroupService groupService) {
        this.topicService = topicService;
        this.groupService = groupService;
    }

    @RequestMapping("topics")
    @Timed(value = "main_page_request_duration", description = "Time taken to return main page", histogram = true)
    public String jiemo(@Parameter Integer groupId, Model model) {
        if (Objects.isNull(groupId)) {
            model.addAttribute("topics", topicService.findAll());
            model.addAttribute("groupName", "所有");
        } else {
            model.addAttribute("topics", topicService.findTopicsByGroupId(groupId));
            groupService.getAllGroups().stream()
                .filter(info -> Objects.equals(info.getGroupId(), groupId))
                .findFirst()
                .ifPresentOrElse(info -> model.addAttribute("groupName", StrUtil.format("{} ({})", info.getGroupName(), info.getGroupId())),
                    () -> model.addAttribute("groupName", "未知"));
        }
        model.addAttribute("groups", groupService.getAllGroups());
        model.addAttribute("uriPrefix", host + ":" + port);
        model.addAttribute("host", host);
        return "jiemo";
    }
}
