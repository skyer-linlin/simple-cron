package com.lin.simplecron.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.lin.simplecron.domain.Comment;
import com.lin.simplecron.domain.Member;
import com.lin.simplecron.domain.Topic;
import com.lin.simplecron.utils.ObjPropsCopyUtil;
import com.lin.simplecron.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
    private static final String COLLECTION_NAME = "jiemo_topic";
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

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
        Topic topic = new Topic();
        // 循环处理 comment, 处理 member, 单独处理富文本属性
        List<Comment> commentList = Lists.newArrayList();
        for (CommentVO commentVO : topicVO.getComments()) {
            Comment comment = new Comment();
            ObjPropsCopyUtil.copyProperties(commentVO, comment);
            comment.setContentUrlTitles(commentVO.getContentUrlTitles());
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

    public List<Topic> readFile2Mongo() {
        List<Topic> topicList = resp2TopicList(applyCustomResponse());
        for (Topic topic : topicList) {
            mongoTemplate.save(topic, COLLECTION_NAME);
        }
        log.info("入库 {} 条 topic, 内容: {}", topicList.size(), JSONUtil.toJsonPrettyStr(topicList));
        return topicList;
    }

    public List<Topic> getAllTopics() {
        List<Topic> topicList = mongoTemplate.findAll(Topic.class, COLLECTION_NAME);
        return topicList;
    }
}
