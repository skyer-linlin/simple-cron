package com.lin.simplecron.web.rest;

import com.lin.simplecron.domain.Topic;
import com.lin.simplecron.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * com.lin.simplecron.controller
 *
 * @author quanlinlin
 * @date 2022/9/13 23:36
 * @since
 */
@RestController
@RequestMapping("/api")
@Tag(name = "主题 api 接口", description = "topic 相关数据导入及查询")
public class TopicController {
    @Autowired
    private TopicService topicService;

    @GetMapping("/readFile2Mongo")
    public ResponseEntity<List<Topic>> readFile2Mongo() {
        List<Topic> topicList = topicService.readFile2Mongo();
        return ResponseEntity.ok(topicList);
    }


    @Operation(summary = "获取所有主题")
    @GetMapping("/topics")
    public ResponseEntity<List<Topic>> getAllTopics() {
        return ResponseEntity.ok(topicService.findAll());
    }

    @GetMapping("/topics/{topicId}")
    public ResponseEntity<Optional<Topic>> getTopic(@PathVariable Integer topicId) {
        Optional<Topic> topic = topicService.findOne(topicId);
        return ResponseEntity.ok(topic);
    }

    @Operation(summary = "获取主题更新")
    @GetMapping("/topics/update/{groupId}")
    public ResponseEntity<List<Topic>> fetchTopicUpdate(@PathVariable @Parameter(example = "37064") Integer groupId) {
        return ResponseEntity.ok(topicService.fetchTopicUpdate(groupId));
    }

    @Operation(summary = "按 groupId 查找主题")
    @GetMapping("/topics/groups/{groupId}")
    public ResponseEntity<List<Topic>> findTopicsByGroupId(@PathVariable Integer groupId) {
        return ResponseEntity.ok(topicService.findTopicsByGroupId(groupId));
    }

    @Operation(summary = "按 groupId 删除最新一条主题")
    @DeleteMapping("/topic/group/{groupId}")
    public ResponseEntity<Optional<Topic>> deleteGroupLatestTopic(@PathVariable @Parameter(example = "42920") Integer groupId) {
        return ResponseEntity.ok(topicService.deleteGroupLatestTopic(groupId));
    }
}
