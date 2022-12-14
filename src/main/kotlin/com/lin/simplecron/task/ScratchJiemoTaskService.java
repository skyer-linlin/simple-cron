package com.lin.simplecron.task;

import com.lin.simplecron.dto.JiemoGroupInfoDto;
import com.lin.simplecron.service.CoinGlassService;
import com.lin.simplecron.service.GroupService;
import com.lin.simplecron.service.LoginTokenService;
import com.lin.simplecron.service.TopicService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

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
    // private final List<Integer> groupIdList = Lists.newArrayList(37064, 41228, 42920, 37012, 34555);
    private final TopicService topicService;
    private final CoinGlassService coinGlassService;
    private final LoginTokenService loginTokenService;
    private final GroupService groupService;

    public ScratchJiemoTaskService(TopicService topicService, CoinGlassService coinGlassService, LoginTokenService loginTokenService, GroupService groupService) {
        this.topicService = topicService;
        this.coinGlassService = coinGlassService;
        this.loginTokenService = loginTokenService;
        this.groupService = groupService;
    }

    /**
     *
     */
    @SneakyThrows
    public void scheduleScratchJiemoTopicTask() {
        log.info("开始抓取芥末圈定时任务");
        Set<JiemoGroupInfoDto> groups = groupService.getAllGroups();
        for (JiemoGroupInfoDto groupInfoDto : groups) {
            Integer groupId = groupInfoDto.getGroupId();
            log.info("抓取 {} 主题中", groupId);
            try {
                topicService.fetchTopicUpdate(groupId);
            } catch (Exception e) {
                log.error("exception catch: 抓取 {} 圈子 {}, 异常详情:", groupInfoDto.getGroupName(), groupId, e);
            } finally {
                log.info("抓取 {} 主题完成", groupId);
                Thread.sleep(7_000);
            }
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

    @SneakyThrows
    public void scheduleClearHistoryFundingRateTask() {
        log.info("开始清理过期资金费率数据");
        coinGlassService.deleteHistoryFundingRate();
        log.info("完成清理过期资金费率数据");
    }

    public void scheduleCheckLoginTokenExpires() {
        log.info("开始检查 loginToken 过期时间");
        loginTokenService.checkLoginTokenExpired();
        log.info("完成检查 loginToken 过期时间");
    }
}
