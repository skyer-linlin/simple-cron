package com.lin.simplecron.task;

import com.google.common.collect.Lists;
import com.lin.simplecron.service.CoinGlassService;
import com.lin.simplecron.service.TopicService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ScratchJiemoTask {
    private final List<Integer> groupIdList = Lists.newArrayList(37064, 41228, 42920, 37012);
    @Autowired
    private TopicService topicService;
    @Autowired
    private CoinGlassService coinGlassService;

    /**
     * 从早 8 到凌晨每2 小时的 15 分运行一次
     */
    @SneakyThrows
    @Scheduled(cron = "0 7 * * * ? ")
    public void scheduleScratchJiemoTopicTask() {
        log.info("开始抓取芥末圈定时任务");
        for (Integer groupId : groupIdList) {
            log.info("抓取 {} 主题中", groupId);
            topicService.fetchTopicUpdate(groupId);
            log.info("抓取 {} 主题完成", groupId);
            Thread.sleep(10_000);
        }
    }

    /**
     * 从早 8 到凌晨每2 小时的 15 分运行一次
     */
    @SneakyThrows
    @Scheduled(cron = "0 * * * * ? ")
    public void scheduleScratchFundingRateTask() {
        log.info("开始抓取资金费率定时任务");
        coinGlassService.saveCurrentFundingRate();
        log.info("完成抓取资金费率定时任务");
    }
}
