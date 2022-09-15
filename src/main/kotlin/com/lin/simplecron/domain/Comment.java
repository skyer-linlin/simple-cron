package com.lin.simplecron.domain;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * com.lin.simplecron.vo
 *
 * @author quanlinlin
 * @date 2022/9/14 17:03
 * @since
 */
@Data
@ToString
@Accessors(chain = true)
public class Comment {
    private Integer id;

    private Integer topicId;

    private Integer userId;

    private String type;

    private Integer groupMemberId;

    private String content;

    private List<String> contentUrlTitles;

    private List<String> multimedia;

    private Integer replyCommentId;

    private Integer replyUserId;

    private Integer replyGroupMemberId;

    private Integer status;

    private Integer topCommentId;

    private Integer groupId;

    private Integer isOwner;

    private String nickname;
    private Integer position;

    private String replyNickname;

    private String replyPosition;

    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private LocalDateTime createTime;
}
