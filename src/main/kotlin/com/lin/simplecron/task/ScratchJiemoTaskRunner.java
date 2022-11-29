package com.lin.simplecron.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * com.lin.simplecron.task
 *
 * @author quanlinlin
 * @date 2022/11/8 01:29
 * @since
 */
@Component
@EnableAsync
@Profile("prod")  // 仅在 prod 配置下执行定时任务
public class ScratchJiemoTaskRunner {
    @Autowired
    private ScratchJiemoTaskService scratchJiemoTaskService;

    @Scheduled(cron = "0 7 * * * ? ")
    @Async("scheduledExecutor")
    public void scheduleScratchJiemoTopicTask() {
        scratchJiemoTaskService.scheduleScratchJiemoTopicTask();
    }

    /**
     * 定时抓取费率
     */
    @Scheduled(cron = "0 * * * * ? ")
    @Async("scheduledExecutor")
    public void scheduleScratchFundingRateTask() {
        scratchJiemoTaskService.scheduleScratchFundingRateTask();
    }

    /**
     * 每 4 个小时检查一次 token 过期时间
     */
    @Scheduled(cron = "0 8 0/4 * * ? ")
    @Async("scheduledExecutor")
    public void scheduleCheckLoginTokenExpires() {
        scratchJiemoTaskService.scheduleCheckLoginTokenExpires();
    }


    /**
     * 定时删除历史费率数据
     */
    @Scheduled(cron = "0 0 2 * * ? ")
    @Async("scheduledExecutor")
    public void scheduleClearHistoryFundingRateTask() {
        scratchJiemoTaskService.scheduleClearHistoryFundingRateTask();
    }
}
