package com.lin.simplecron.web.rest;

import com.lin.simplecron.dto.JiemoGroupInfoDto;
import com.lin.simplecron.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

/**
 * com.lin.simplecron.web.rest
 *
 * @author quanlinlin
 * @date 2022/11/21 06:30
 * @since
 */
@RestController
@RequestMapping("/api/groups/")
@Tag(name = "group api 接口", description = "group 信息管理")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Operation(summary = "添加一个新的 group 抓取")
    @PostMapping("/add/{groupId}")
    public ResponseEntity<Optional<JiemoGroupInfoDto>> addGroup(@PathVariable @Parameter Integer groupId) {
        return ResponseEntity.ok(groupService.addGroup(groupId));
    }

    @Operation(summary = "获取所有 group 信息")
    @GetMapping("/all")
    public ResponseEntity<Set<JiemoGroupInfoDto>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @Operation(summary = "移除不想继续抓取的 group")
    @DeleteMapping("/remove/{groupId}")
    public ResponseEntity<Optional<JiemoGroupInfoDto>> removeGroup(@PathVariable @Parameter Integer groupId) {
        return ResponseEntity.ok(groupService.removeGroup(groupId));
    }

    @Operation(summary = "隐藏不想继续抓取的 group,与移除 api 不同的是并不删除已经抓取的内容")
    @PostMapping("/hide/{groupId}")
    public ResponseEntity<Optional<JiemoGroupInfoDto>> hideGroup(@PathVariable @Parameter Integer groupId) {
        return ResponseEntity.ok(groupService.hideGroup(groupId));
    }
}
