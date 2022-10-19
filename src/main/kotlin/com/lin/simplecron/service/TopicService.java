package com.lin.simplecron.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.lin.simplecron.domain.Comment;
import com.lin.simplecron.domain.Member;
import com.lin.simplecron.domain.Topic;
import com.lin.simplecron.repository.TopicRepository;
import com.lin.simplecron.utils.ObjPropsCopyUtil;
import com.lin.simplecron.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * com.lin.simplecron.service
 *
 * @author quanlinlin
 * @date 2022/9/14 21:47
 * @since
 */
@Service
@Slf4j
public class TopicService {
    private static String JIEMO_TOPIC_URL =
        "https://api320.jiemo100.com/topic/index/index?login_token={loginToken}&group_id={groupId}&page=1&page_size=10";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ImService imService;
    @Autowired
    private LoginTokenService loginTokenService;

    public CustomResponse applyCustomResponse() {
        log.info("读取本地 json 文件");

        String str = FileUtil.readString("/Users/cicada/Downloads/jiemo_content_fetch/zhoulang_0913.json", StandardCharsets.UTF_8);
        CustomResponse response = null;
        try {
            response = objectMapper.readValue(str, CustomResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing json");
            e.printStackTrace();
        }
        return response;
    }


    /**
     * 将抓取到的响应转换为对象供使用
     *
     * @param ctResp
     * @return
     */
    public List<Topic> resp2TopicList(CustomResponse ctResp) {
        List<TopicVO> topicVOS = ctResp.getData().getTopics();
        List<Topic> topicList = new ArrayList<>();
        for (TopicVO topicVO : topicVOS) {
            Topic topic = topicVO2Topic(topicVO);
            topicList.add(topic);
        }
        return topicList;
    }

    private Topic topicVO2Topic(TopicVO topicVO) {
        if (topicVO == null) {
            return null;
        }
        Topic topic = new Topic();
        // 循环处理 comment, 处理 member, 单独处理富文本属性
        List<Comment> commentList = Lists.newArrayList();
        for (CommentVO commentVO : topicVO.getComments()) {
            Comment comment = new Comment();
            ObjPropsCopyUtil.copyProperties(commentVO, comment);
            comment.setContentUrlTitles(commentVO.getContentUrlTitles().stream().map(ContentUrlVO::getUrl).collect(Collectors.toList()));
            comment.setMultimedia(commentVO.getMultimedia());
            commentList.add(comment);
        }
        MemberVO memberVO = topicVO.getMember();
        Member member = null;
        if (memberVO != null) {
            member = new Member();
            ObjPropsCopyUtil.copyProperties(memberVO, member);
        }
        // 用反射自行自制工具类复制属性,从 string 复制到 int
        ObjPropsCopyUtil.copyProperties(topicVO, topic);
        // 手动设置集合和对象属性
        topic.setComments(commentList);
        topic.setMember(member);
        topic.setMultimedia(topicVO.getMultimedia().stream().map(MutiMediaVO::getUrl).collect(Collectors.toList()));
        log.debug("origin topicVO: {}", JSONUtil.toJsonPrettyStr(topicVO));
        log.info("-----");
        log.debug("target topic domain: {}", JSONUtil.toJsonPrettyStr(topic));
        return topic;
    }

    @Transactional
    public List<Topic> readFile2Mongo() {
        List<Topic> topicList = resp2TopicList(applyCustomResponse());
        topicRepository.saveAll(topicList);
        log.info("入库 {} 条 topic, 内容: {}", topicList.size(), JSONUtil.toJsonPrettyStr(topicList));
        return topicList;
    }

    @Transactional(readOnly = true)
    public List<Topic> findAll() {
        Sort sort = Sort.sort(Topic.class).by(Topic::getCreateTime).descending();
        List<Topic> topicList = topicRepository.findAll(sort);
        for (Topic topic : topicList) {
            // 有权限要求的圈子,content 字段只有前几十个字,剩余内容在ContentVisitor字段,无门槛圈子,主题内容都在 content 字段
            if (StrUtil.isBlank(topic.getContentVisitor())) {
                topic.setContentVisitor(topic.getContent());
            } else {
                String preStr = StrUtil.subPre(topic.getContent(), 6);
                topic.setContentVisitor(preStr + topic.getContentVisitor());
            }
        }
        return topicList;
    }


    public Optional<Topic> findOne(Integer topicId) {
        return topicRepository.findById(topicId);
    }

    @Transactional
    public List<Topic> fetchTopicUpdate(Integer groupId) {
        CustomResponse customResponse = fetchTopicData(groupId);
        List<TopicVO> topicVOList = customResponse.getData().getTopics();
        String groupName = topicVOList.get(0).getGroup().getTitle();
        List<Topic> topicList = topicVOList.stream().map(this::topicVO2Topic).collect(Collectors.toList());
        // 通过查找最近更新的主题, 比较是否有更新
        Optional<Topic> latestTopic = findGroupLatestTopic(groupId);
        latestTopic.ifPresent(lt -> {
            long newTopicCount = topicList.stream().filter(topic -> topic.getCreateTime().isAfter(lt.getCreateTime())).count();
            log.info("你关注的芥末圈 {} 有 {} 条新主题了", groupName, newTopicCount);
            topicList.stream().filter(topic -> topic.getCreateTime().isAfter(lt.getCreateTime()))
                .max(Comparator.comparing(Topic::getCreateTime)).ifPresent(topic -> {
                        if (newTopicCount > 0) {
                            // 发送 im 通知和邮件通知
                            String msgTemplate = """
                                你关注的芥末圈 💥{}💥 有 {} 条新主题了, 内容:
                                                                
                                {}
                                                                
                                ---完---
                                """;
                            imService.sendImMsg(StrUtil.format(msgTemplate,
                                groupName, newTopicCount, topic.getContent()));
                            // mailService.notice(StrUtil.format("你关注的芥末圈 💥{}💥 有 {} 条新主题了", groupName, newTopicCount), topic.getContent());
                        }
                    }
                );
        });
        List<Topic> result = topicRepository.saveAll(topicList);
        log.info("对 {} 芥末圈 {} 更新主题", groupName, groupId);
        return result;
    }

    public Optional<Topic> findGroupLatestTopic(Integer groupId) {
        Example<Topic> example = Example.of(new Topic().setGroupId(groupId));
        List<Topic> topicList = topicRepository.findAll(example);
        Optional<Topic> max = topicList.stream().max(Comparator.comparing(Topic::getCreateTime));
        return max;
    }

    @Nullable
    private CustomResponse fetchTopicData(Integer groupId) {
        HttpHeaders httpHeaders = setRequestHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
        String currentLoginToken = loginTokenService.getCurrentLoginToken();
        ResponseEntity<CustomResponse> responseEntity = restTemplate.exchange(JIEMO_TOPIC_URL, HttpMethod.GET, requestEntity, CustomResponse.class, currentLoginToken, groupId);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            log.error("抓取 topic 错误, 错误码:{}", responseEntity.getStatusCode());
            return null;
        }
    }

    private HttpHeaders setRequestHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", "application/json, text/plain, */*");
        httpHeaders.set("Accept-Language", "zh-CN,zh;q=0.9");
        httpHeaders.set("Connection", "keep-alive");
        httpHeaders.set("Origin", "https://web.jemoo.cn");
        httpHeaders.set("Referer", "https://web.jemoo.cn/");
        httpHeaders.set("Sec-Fetch-Dest", "empty");
        httpHeaders.set("Sec-Fetch-Mode", "cors");
        httpHeaders.set("Sec-Fetch-Site", "cross-site");
        httpHeaders.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36");
        httpHeaders.set("X-APP-SOURCE", "web_pc");
        httpHeaders.set("X-APP-VERSION", "2.8.0");
        httpHeaders.set("noncestr", "7594194731");
        httpHeaders.set("sec-ch-ua", "\"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"");
        httpHeaders.set("sec-ch-ua-mobile", "?0");
        httpHeaders.set("sec-ch-ua-platform", "\"macOS\"");
        httpHeaders.set("sign", "72660f5b8cd2777f3e220ea4f17b773cbb6300");
        httpHeaders.set("timestamp", "1663054705567");
        httpHeaders.set("Cookie", "PHPSESSID=401d58286840994ed4b36cbfcfb0e6d7");
        return httpHeaders;
    }

    public List<Topic> findTopicsByGroupId(Integer groupId) {
        Topic topic = new Topic().setGroupId(groupId);
        List<Topic> groupTopics = topicRepository.findAll(Example.of(topic));
        return groupTopics;
    }


    public Optional<Topic> deleteGroupLatestTopic(Integer groupId) {
        Optional<Topic> groupLatestTopic = findGroupLatestTopic(groupId);
        groupLatestTopic.ifPresent(topic -> {
            topicRepository.deleteById(topic.getId());
            log.warn("删除 {} 圈子 id为 {} 主题", groupId, topic.getId());
        });
        return groupLatestTopic;
    }
}
