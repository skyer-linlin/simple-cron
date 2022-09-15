package com.lin.simplecron.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * jiemofetch.domains
 *
 * @author quanlinlin
 * @date 2022/9/13 20:21
 * @since
 */
@Data
public class LikesVO {

  private String id;

  @JsonProperty("topic_id")
  private String topicId;

  @JsonProperty("for_user_id")
  private String forUserId;

  @JsonProperty("user_id")
  private String userId;

  @JsonProperty("group_member_id")
  private String groupMemberId;

  @JsonProperty("create_time")
  private String createTime;

  @JsonProperty("group_id")
  private String groupId;

  private String memo;
  private String nickname;
}
