package com.lin.simplecron.web.rest;

import com.lin.simplecron.domain.Topic;
import com.lin.simplecron.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * com.lin.simplecron.controller
 *
 * @author quanlinlin
 * @date 2022/9/13 23:36
 * @since
 */
@RestController
@RequestMapping("/topic")
public class TopicController {
    @Autowired
    private TopicService topicService;

    @GetMapping("/all")
    public ResponseEntity<List<Topic>> all() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    @GetMapping("/readFile2Mongo")
    public ResponseEntity<List<Topic>> readFile2Mongo() {
        List<Topic> topicList = topicService.readFile2Mongo();
        return ResponseEntity.ok(topicList);
    }
}
