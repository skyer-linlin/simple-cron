package com.lin.simplecron.service;

import cn.hutool.core.collection.CollectionUtil;
import com.lin.simplecron.config.Constants;
import com.lin.simplecron.dto.JiemoGroupInfoDto;
import com.lin.simplecron.utils.RedisUtil;
import com.lin.simplecron.vo.JiemoResponse;
import com.lin.simplecron.vo.TopicVO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * com.lin.simplecron.service
 *
 * @author quanlinlin
 * @date 2022/11/21 06:34
 * @since
 */
@Service
@Slf4j
public class GroupService {
    public static final String JIEMO_GROUPS_SET_CACHE_KEY =
        RedisUtil.getStandardKeyName(Constants.CacheConsts.JIEMO_GROUPS_SET, Constants.RedisValueType.SET);
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TopicService topicService;

    public Set<JiemoGroupInfoDto> getAllGroups() {
        return (Set) redisTemplate.opsForSet().members(JIEMO_GROUPS_SET_CACHE_KEY);
    }

    @SneakyThrows
    public Optional<JiemoGroupInfoDto> addGroup(Integer groupId) {
        // 实际上因为加了cronStartTime字段,导致使用 set 类型没有什么意义,不同的时间添加同一个 groupId 产生的对象是不同的,所以此处手动去重
        List<JiemoGroupInfoDto> collect = getAllGroups().stream()
            .filter(dto -> Objects.equals(dto.getGroupId(), groupId)).collect(Collectors.toList());
        if (collect.size() > 0) {
            return collect.stream().findAny();
        }

        JiemoResponse jiemoResponse = topicService.fetchTopicData(groupId);
        List<TopicVO> topics = jiemoResponse.getData().getTopics();
        if (CollectionUtil.isNotEmpty(topics)) {
            TopicVO topicVO = topics.get(0);
            JiemoGroupInfoDto dto = new JiemoGroupInfoDto()
                .setGroupId(groupId)
                .setOwner(topicVO.getMember().getNickname())
                .setGroupName(topicVO.getGroup().getTitle())
                .setCreateTime(LocalDateTime.now())
                .setCronStartTime(LocalDateTime.now());
            redisTemplate.opsForSet().add(JIEMO_GROUPS_SET_CACHE_KEY, dto);
            return Optional.ofNullable(dto);
        }
        Thread.sleep(10_000);
        topicService.fetchTopicUpdate(groupId);
        return Optional.empty();
    }

    public Optional<JiemoGroupInfoDto> removeGroup(Integer groupId) {
        Optional<JiemoGroupInfoDto> groupInfoDto = getAllGroups().stream()
            .filter(dto -> Objects.equals(dto.getGroupId(), groupId)).findAny();
        groupInfoDto.ifPresent(info -> {
            redisTemplate.opsForSet().remove(JIEMO_GROUPS_SET_CACHE_KEY, info);
            log.warn("即将删除圈子: {}, groupId: {}", info.getGroupName(), groupId);
            topicService.deleteGroupTopics(groupId);
        });
        return groupInfoDto;
    }

    public Optional<JiemoGroupInfoDto> hideGroup(Integer groupId) {
        Optional<JiemoGroupInfoDto> groupInfoDto = getAllGroups().stream()
            .filter(dto -> Objects.equals(dto.getGroupId(), groupId)).findAny();
        groupInfoDto.ifPresent(info -> {
            redisTemplate.opsForSet().remove(JIEMO_GROUPS_SET_CACHE_KEY, info);
            log.warn("即将删除圈子: {}, groupId: {}", info.getGroupName(), groupId);
        });
        return groupInfoDto;
    }
}
