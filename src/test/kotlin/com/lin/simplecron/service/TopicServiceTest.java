package com.lin.simplecron.service;

import com.lin.simplecron.vo.JiemoResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * com.lin.simplecron.service
 *
 * @author quanlinlin
 * @date 2022/9/15 03:50
 * @since
 */
@SpringBootTest
class TopicServiceTest {
  @Autowired private TopicService topicService;

  @Test
  void applyCustomResponse() {
    JiemoResponse customResponse = topicService.applyCustomResponse();
    Assertions.assertEquals(10, customResponse.getData().getTopics().size());
  }
}
