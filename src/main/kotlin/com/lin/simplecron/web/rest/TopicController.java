package com.lin.simplecron.web.rest;

import com.lin.simplecron.domain.Topic;
import com.lin.simplecron.dto.TopicDto;
import com.lin.simplecron.service.TopicService;
import com.lin.simplecron.task.ScratchJiemoTaskService;
import io.micrometer.core.annotation.Timed;
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
    @Autowired
    private ScratchJiemoTaskService scratchJiemoTask;

    @GetMapping("/readFile2Mongo")
    @Timed(value = "main_page_request_duration", description = "Time taken to return main page", histogram = true)
    public ResponseEntity<List<Topic>> readFile2Mongo() {
        List<Topic> topicList = topicService.readFile2Mongo();
        return ResponseEntity.ok(topicList);
    }


    @Operation(summary = "获取所有主题")
    @Timed(value = "main_page_request_duration", description = "Time taken to return main page", histogram = true)
    @GetMapping("/topics")
    public ResponseEntity<List<TopicDto>> getAllTopics() {
        return ResponseEntity.ok(topicService.findAll());
    }

    @GetMapping("/topics/{topicId}")
    @Timed(value = "main_page_request_duration", description = "Time taken to return main page", histogram = true)
    public ResponseEntity<Optional<TopicDto>> getTopic(@PathVariable Integer topicId) {
        Optional<TopicDto> topic = topicService.findOne(topicId);
        return ResponseEntity.ok(topic);
    }

    @Operation(summary = "抓取最新圈子内容")
    @Timed(value = "main_page_request_duration", description = "Time taken to return main page", histogram = true)
    @GetMapping("/topics/update/{groupId}")
    public ResponseEntity<List<Topic>> fetchTopicUpdate(@PathVariable @Parameter(example = "37064") Integer groupId) {
        return ResponseEntity.ok(topicService.fetchTopicUpdate(groupId));
    }

    @Operation(summary = "按 groupId 查找主题")
    @Timed(value = "main_page_request_duration", description = "Time taken to return main page", histogram = true)
    @GetMapping("/topics/groups/{groupId}")
    public ResponseEntity<List<TopicDto>> findTopicsByGroupId(@PathVariable Integer groupId) {
        return ResponseEntity.ok(topicService.findTopicsByGroupId(groupId));
    }

    @Operation(summary = "按 groupId 删除最新一条主题")
    @DeleteMapping("/topics/group/{groupId}")
    @Timed(value = "main_page_request_duration", description = "Time taken to return main page", histogram = true)
    public ResponseEntity<Optional<Topic>> deleteGroupLatestTopic(@PathVariable @Parameter(example = "42920") Integer groupId) {
        return ResponseEntity.ok(topicService.deleteGroupLatestTopic(groupId));
    }

    @Operation(summary = "执行一次定时任务更新")
    @PostMapping("/topics/task/fetchAll")
    @Timed(value = "main_page_request_duration", description = "Time taken to return main page", histogram = true)
    public ResponseEntity<String> findTopicsByGroupId() {
        scratchJiemoTask.scheduleScratchJiemoTopicTask();
        return ResponseEntity.ok("更新完成");
    }

    @Operation(summary = "按照给定 id 批量扫描芥末圈内容")
    @PostMapping("/topics/scan/{startGroupId}/{endGroupId}")
    public void batchScanGroupTopics(@PathVariable Integer startGroupId, @PathVariable Integer endGroupId) {
        topicService.batchScanGroupTopics(startGroupId, endGroupId);
    }
}
