package com.lin.simplecron.task;

import com.google.common.collect.Lists;
import com.lin.simplecron.service.CoinGlassService;
import com.lin.simplecron.service.TopicService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * com.lin.simplecron.task
 *
 * @author quanlinlin
 * @date 2022/9/17 22:33
 * @since
 */
@Component
@Slf4j
public class ScratchJiemoTaskService {
    private final List<Integer> groupIdList = Lists.newArrayList(37064, 41228, 42920, 37012, 34555);
    private final TopicService topicService;
    private final CoinGlassService coinGlassService;

    public ScratchJiemoTaskService(TopicService topicService, CoinGlassService coinGlassService) {
        this.topicService = topicService;
        this.coinGlassService = coinGlassService;
    }

    /**
     *
     */
    @SneakyThrows
    public void scheduleScratchJiemoTopicTask() {
        log.info("开始抓取芥末圈定时任务");
        for (Integer groupId : groupIdList) {
            log.info("抓取 {} 主题中", groupId);
            topicService.fetchTopicUpdate(groupId);
            log.info("抓取 {} 主题完成", groupId);
            Thread.sleep(7_000);
        }
    }

    /**
     *
     */
    @SneakyThrows
    public void scheduleScratchFundingRateTask() {
        log.info("开始抓取资金费率定时任务");
        coinGlassService.saveCurrentFundingRate();
        log.info("完成抓取资金费率定时任务");
    }
}
