package com.lin.simplecron.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author ：mmzsblog.cn
 * @date ：Created in 2021/7/27 17:46
 * @description：spring-boot 多线程  @Scheduled注解 并发定时任务的解决方案
 * @modified By：
 * @version:
 */

@Configuration
public class ScheduledTaskConfig {

    public static final String EXECUTOR_SERVICE = "scheduledExecutor";


    @Bean(EXECUTOR_SERVICE)
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
        // 设置最大线程数
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 10);
        // 设置队列容量
        executor.setQueueCapacity(Runtime.getRuntime().availableProcessors() * 10);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(10);
        // 设置默认线程名称
        executor.setThreadNamePrefix("scheduled-");
        // 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //执行初始化
        executor.initialize();
        return executor;
    }

}

