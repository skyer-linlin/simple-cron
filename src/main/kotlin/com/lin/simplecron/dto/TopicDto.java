package com.lin.simplecron.dto;

import com.lin.simplecron.domain.Topic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * com.lin.simplecron.dto
 *
 * @author quanlinlin
 * @date 2022/11/23 06:45
 * @since
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class TopicDto extends Topic {
    private List<CommentDto> commentDtoList;
}
