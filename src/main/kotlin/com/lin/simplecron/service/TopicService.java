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
import com.lin.simplecron.dto.CommentDto;
import com.lin.simplecron.dto.JiemoGroupInfoDto;
import com.lin.simplecron.dto.TopicDto;
import com.lin.simplecron.repository.TopicRepository;
import com.lin.simplecron.utils.CommentTreeUtil;
import com.lin.simplecron.utils.ObjPropsCopyUtil;
import com.lin.simplecron.vo.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
    private final ObjectMapper objectMapper;
    private final TopicRepository topicRepository;
    private final RestTemplate restTemplate;
    private final ImService imService;
    private final LoginTokenService loginTokenService;
    private final GroupService groupService;

    public TopicService(ObjectMapper objectMapper,
                        TopicRepository topicRepository,
                        @Qualifier("NoProxyRestTemplate") RestTemplate restTemplate,
                        ImService imService,
                        LoginTokenService loginTokenService,
                        GroupService groupService) {
        this.objectMapper = objectMapper;
        this.topicRepository = topicRepository;
        this.restTemplate = restTemplate;
        this.imService = imService;
        this.loginTokenService = loginTokenService;
        this.groupService = groupService;
    }

    public JiemoResponse applyCustomResponse() {
        log.info("???????????? json ??????");

        String str = FileUtil.readString("/Users/cicada/Downloads/jiemo_content_fetch/zhoulang_0913.json", StandardCharsets.UTF_8);
        JiemoResponse response = null;
        try {
            response = objectMapper.readValue(str, JiemoResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing json");
            e.printStackTrace();
        }
        return response;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param ctResp
     * @return
     */
    public List<Topic> resp2TopicList(JiemoResponse ctResp) {
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
        // ???????????? comment, ?????? member, ???????????????????????????
        List<Comment> commentList = Lists.newArrayList();
        for (CommentVO commentVO : topicVO.getComments()) {
            Comment comment = new Comment();
            ObjPropsCopyUtil.copyProperties(commentVO, comment);
            comment.setContentUrlTitles(commentVO.getContentUrlTitles().stream().map(ContentUrlVO::getUrl).collect(Collectors.toList()));
            comment.setMultimedia(commentVO.getMultimedia().stream().map(MutiMediaVO::getUrl).collect(Collectors.toList()));
            commentList.add(comment);
        }
        MemberVO memberVO = topicVO.getMember();
        Member member = null;
        if (memberVO != null) {
            member = new Member();
            ObjPropsCopyUtil.copyProperties(memberVO, member);
        }
        // ??????????????????????????????????????????,??? string ????????? int
        ObjPropsCopyUtil.copyProperties(topicVO, topic);
        // ?????????????????????????????????
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
        log.info("?????? {} ??? topic, ??????: {}", topicList.size(), JSONUtil.toJsonPrettyStr(topicList));
        return topicList;
    }

    @Transactional(readOnly = true)
    public List<TopicDto> findAll() {
        Sort sort = Sort.sort(Topic.class).by(Topic::getCreateTime).descending();
        List<Topic> topicList = topicRepository.findAll(sort);
        topicContent(topicList);
        // filter, ??????????????????????????????????????????
        filterMeanlessContent(topicList);
        Map<Integer, JiemoGroupInfoDto> groupMap = groupService.getGroupMap();
        List<TopicDto> topicDtoList = Lists.newArrayList();
        for (Topic topic : topicList) {
            TopicDto topicDto = topic2TopicDto(topic, groupMap);
            topicDtoList.add(topicDto);
        }
        return topicDtoList;
    }

    public List<TopicDto> findLeastRecently() {
        LocalDate startDate = LocalDate.now().minusDays(10);
        List<Topic> topicList = topicRepository.findTopicsByCreateTimeAfterOrderByCreateTimeDesc(startDate);
        topicContent(topicList);
        // filter, ??????????????????????????????????????????
        filterMeanlessContent(topicList);
        Map<Integer, JiemoGroupInfoDto> groupMap = groupService.getGroupMap();
        List<TopicDto> topicDtoList = Lists.newArrayList();
        for (Topic topic : topicList) {
            TopicDto topicDto = topic2TopicDto(topic, groupMap);
            topicDtoList.add(topicDto);
        }
        return topicDtoList;
    }

    /**
     * ??????????????????????????????
     *
     * @param topicList
     */
    private void filterMeanlessContent(List<Topic> topicList) {
        String[] meanLessWords = new String[]{"??????", "??????", "???", "?????????", "??????"};
        Iterator<Topic> iterator = topicList.iterator();
        while (iterator.hasNext()) {
            Topic topic = iterator.next();
            // ????????????????????????????????????????????????,????????????????????????????????????
            if (Objects.equals(topic.getGroupId(), 37028)) {
                String contentVisitor = topic.getContentVisitor();
                if (contentVisitor.length() < 10 && StrUtil.containsAny(contentVisitor, meanLessWords)) {
                    iterator.remove();
                }
                // ????????????????????????????????????
                List<Comment> commentList = topic.getComments();
                commentList.removeIf(comment -> comment.getContent().length() < 10 && StrUtil.containsAny(comment.getContent(), meanLessWords));
            }
        }

    }

    private TopicDto topic2TopicDto(Topic topic, Map<Integer, JiemoGroupInfoDto> groupMap) {
        TopicDto topicDto = new TopicDto();
        BeanUtils.copyProperties(topic, topicDto);
        List<CommentDto> commentDtos = CommentTreeUtil.buildSortedCommentsList(topic.getComments());
        topicDto.setCommentDtoList(commentDtos);
        topicDto.setGroupName(groupMap.getOrDefault(topic.getGroupId(), new JiemoGroupInfoDto().setGroupName("??????")).getGroupName());
        return topicDto;
    }

    private void topicContent(List<Topic> topicList) {
        for (Topic topic : topicList) {
            // ????????????????????????,content ???????????????????????????,???????????????ContentVisitor??????,???????????????,?????????????????? content ??????
            if (StrUtil.isBlank(topic.getContentVisitor())) {
                topic.setContentVisitor(topic.getContent());
            } else {
                String preStr = StrUtil.subPre(topic.getContent(), 6);
                topic.setContentVisitor(preStr + topic.getContentVisitor());
            }
            topic.setContentVisitor(StrUtil.trim(topic.getContentVisitor()));
        }
    }

    public List<TopicDto> findTopicsByGroupId(Integer groupId) {
        Topic topic = new Topic().setGroupId(groupId);
        Sort sort = Sort.sort(Topic.class).by(Topic::getCreateTime).descending();
        List<Topic> groupTopics = topicRepository.findAll(Example.of(topic), sort);
        topicContent(groupTopics);
        filterMeanlessContent(groupTopics);
        List<TopicDto> topicDtoList = Lists.newArrayList();
        Map<Integer, JiemoGroupInfoDto> groupMap = groupService.getGroupMap();
        for (Topic gt : groupTopics) {
            TopicDto topicDto = topic2TopicDto(gt, groupMap);
            topicDtoList.add(topicDto);
        }
        return topicDtoList;
    }

    public Optional<TopicDto> findOne(Integer topicId) {
        Optional<Topic> topic = topicRepository.findById(topicId);
        Map<Integer, JiemoGroupInfoDto> groupMap = groupService.getGroupMap();
        return topic.map(topic1 -> topic2TopicDto(topic1, groupMap));
    }

    @Transactional
    public List<Topic> fetchTopicUpdate(Integer groupId) {
        JiemoResponse customResponse = fetchTopicData(groupId);
        List<TopicVO> topicVOList = customResponse.getData().getTopics();
        String groupName = topicVOList.get(0).getGroup().getTitle();
        List<Topic> topicList = topicVOList.stream().map(this::topicVO2Topic).collect(Collectors.toList());
        // ?????????????????????????????????, ?????????????????????
        Optional<Topic> latestTopic = findGroupLatestTopic(groupId);
        latestTopic.ifPresent(lt -> {
            long newTopicCount = topicList.stream().filter(topic -> topic.getCreateTime().isAfter(lt.getCreateTime())).count();
            log.info("????????????????????? {} ??? {} ???????????????", groupName, newTopicCount);
            topicList.stream().filter(topic -> topic.getCreateTime().isAfter(lt.getCreateTime()))
                .max(Comparator.comparing(Topic::getCreateTime)).ifPresent(topic -> {
                        if (newTopicCount > 0) {
                            // ?????? im ?????????????????????
                            String msgTemplate = """
                                ????????????????????? ????{}???? ??? {} ???????????????, ??????:
                                                                
                                {}
                                                                
                                ---???---
                                """;
                            imService.sendImMsg(StrUtil.format(msgTemplate,
                                groupName, newTopicCount, topic.getContent()));
                            // mailService.notice(StrUtil.format("????????????????????? ????{}???? ??? {} ???????????????", groupName, newTopicCount), topic.getContent());
                        }
                    }
                );
        });
        List<Topic> result = topicRepository.saveAll(topicList);
        log.info("??? {} ????????? {} ????????????", groupName, groupId);
        return result;
    }

    public Optional<Topic> findGroupLatestTopic(Integer groupId) {
        Example<Topic> example = Example.of(new Topic().setGroupId(groupId));
        List<Topic> topicList = topicRepository.findAll(example);
        Optional<Topic> max = topicList.stream().max(Comparator.comparing(Topic::getCreateTime));
        return max;
    }

    @Nullable
    JiemoResponse fetchTopicData(Integer groupId) {
        HttpHeaders httpHeaders = setRequestHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
        String currentLoginToken = loginTokenService.getCurrentLoginToken();
        ResponseEntity<JiemoResponse> responseEntity = executeHttpFetchRequest(groupId, requestEntity, currentLoginToken);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            log.error("?????? topic ??????, ?????????:{}", responseEntity.getStatusCode());
            return null;
        }
    }

    /**
     * ????????????????????? SocketTimeoutException, ?????????????????????
     *
     * @param groupId
     * @param requestEntity
     * @param currentLoginToken
     * @return
     */
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 5000, multiplier = 1, maxDelay = 10000))
    private ResponseEntity<JiemoResponse> executeHttpFetchRequest(Integer groupId, HttpEntity<String> requestEntity, String currentLoginToken) {
        ResponseEntity<JiemoResponse> responseEntity =
            restTemplate.exchange(JIEMO_TOPIC_URL, HttpMethod.GET, requestEntity, JiemoResponse.class, currentLoginToken, groupId);
        return responseEntity;
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

    public Optional<Topic> deleteGroupLatestTopic(Integer groupId) {
        Optional<Topic> groupLatestTopic = findGroupLatestTopic(groupId);
        groupLatestTopic.ifPresent(topic -> {
            topicRepository.deleteById(topic.getId());
            log.warn("?????? {} ?????? id??? {} ??????", groupId, topic.getId());
        });
        return groupLatestTopic;
    }

    @SneakyThrows
    public void batchScanGroupTopics(Integer startGroupId, Integer endGroupId) {
        // ?????????????????? im,????????????????????????
        // ????????????????????????,????????????????????????
        List<Integer> valueList = new ArrayList<>();
        log.info("????????????????????????????????????,?????? startGroupId:{}, endGroupId:{}", startGroupId, endGroupId);

        for (int currentGroupId = startGroupId + 1; currentGroupId < endGroupId; currentGroupId++) {
            log.info("------------------------------");
            Thread.sleep(10_000);

            log.info("???????????? id:{}", currentGroupId);
            JiemoResponse customResponse = fetchTopicData(currentGroupId);
            List<TopicVO> topicVOList = customResponse.getData().getTopics();
            if (topicVOList.isEmpty()) continue;
            String groupName = topicVOList.get(0).getGroup().getTitle();
            List<Topic> topicList = topicVOList.stream().map(this::topicVO2Topic).collect(Collectors.toList());
            if (topicList.size() < 10) continue;
            log.info("???????????? id:{}, ??????:{}", currentGroupId, groupName);
            LocalDateTime lastUpdateTime = topicList.get(0).getCreateTime();
            log.info("??????????????????:{}", lastUpdateTime);
            if (lastUpdateTime.isAfter(LocalDate.of(2022, 1, 1).atStartOfDay())) {
                valueList.add(currentGroupId);
                imService.sendImMsg(StrUtil.format("{} - {} ????????????,?????????????????????????????????: {}", startGroupId, endGroupId, currentGroupId));
            }
            Topic lastTopic = topicList.get(0);
            if (StrUtil.isBlank(lastTopic.getContentVisitor())) {
                lastTopic.setContentVisitor(lastTopic.getContent());
            } else {
                String preStr = StrUtil.subPre(lastTopic.getContent(), 6);
                lastTopic.setContentVisitor(preStr + lastTopic.getContentVisitor());
            }
            log.info("??????????????????:{}", lastTopic.getContentVisitor());

        }

        log.info("?????????????????????:{}", valueList);
    }

    public void deleteGroupTopics(Integer groupId) {
        Topic topic = new Topic().setGroupId(groupId);
        List<Topic> groupTopics = topicRepository.findAll(Example.of(topic));
        topicRepository.deleteAll(groupTopics);
        log.info("????????? {} ???????????? {} ???", groupId, groupTopics.size());
    }


}
