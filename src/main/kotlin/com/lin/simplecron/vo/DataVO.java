package com.lin.simplecron.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * jiemofetch.domains
 *
 * @author quanlinlin
 * @date 2022/9/13 20:19
 * @since
 */
public class DataVO {

    private int page;
    @JsonProperty("page_size")
    private int pageSize;
    private List<TopicVO> topics;

    public void setPage(int page) {
        this.page = page;
    }
    public int getPage() {
        return this.page;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public int getPageSize() {
        return this.pageSize;
    }
    public void setTopics(List<TopicVO> topics) {
        this.topics = topics;
    }
    public List<TopicVO> getTopics() {
        return this.topics;
    }
}