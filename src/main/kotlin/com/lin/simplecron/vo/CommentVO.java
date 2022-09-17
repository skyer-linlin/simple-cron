package com.lin.simplecron.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * com.lin.simplecron.vo
 *
 * @author quanlinlin
 * @date 2022/9/14 17:03
 * @since
 */
@Data
public class CommentVO {
    private String id;

    @JsonProperty("topic_id")
    private String topicId;

    @JsonProperty("user_id")
    private String userId;

    private String type;

    @JsonProperty("group_member_id")
    private String groupMemberId;

    private String content;

    @JsonProperty("content_url_titles")
    private List<ContentUrlVO> contentUrlTitles;

    private List<String> multimedia;

    @JsonProperty("reply_comment_id")
    private String replyCommentId;

    @JsonProperty("reply_user_id")
    private String replyUserId;

    @JsonProperty("reply_group_member_id")
    private String replyGroupMemberId;

    private String status;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("top_comment_id")
    private String topCommentId;

    @JsonProperty("group_id")
    private String groupId;

    @JsonProperty("is_owner")
    private String isOwner;

    private String nickname;
    private String position;
    @JsonProperty("headimgurl")
    private String headImgUrl;

    @JsonProperty("reply_nickname")
    private String replyNickname;

    @JsonProperty("reply_position")
    private String replyPosition;
}
