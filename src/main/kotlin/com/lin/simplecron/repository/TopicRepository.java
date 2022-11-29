package com.lin.simplecron.repository;

import com.lin.simplecron.domain.Topic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * com.lin.simplecron.repository
 *
 * @author quanlinlin
 * @date 2022/9/16 01:38
 * @since
 */
public interface TopicRepository extends MongoRepository<Topic, Integer> {

    List<Topic> findTopicsByCreateTimeAfterOrderByCreateTimeDesc(LocalDate startDate);
}
