package com.lin.simplecron.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * jiemofetch.domains
 *
 * @author quanlinlin
 * @date 2022/9/13 20:20
 * @since
 */
@Data
public class TopicVO {

  private String id;

  @JsonProperty("group_id")
  private String groupId;

  @JsonProperty("group_member_id")
  private String groupMemberId;

  @JsonProperty("user_id")
  private String userId;

  private String type;
  private String preface;
  private String content;

  @JsonProperty("rich_content")
  private String richContent;

  @JsonProperty("content_url_titles")
  private List<ContentUrlVO> contentUrlTitles;

  private List<MutiMediaVO> multimedia;
  private String money;
  private String status;

  @JsonProperty("stick_time")
  private String stickTime;

  @JsonProperty("essence_time")
  private String essenceTime;

  @JsonProperty("comment_count")
  private String commentCount;

  @JsonProperty("admire_count")
  private String admireCount;

  @JsonProperty("like_count")
  private String likeCount;

  @JsonProperty("share_count")
  private String shareCount;

  @JsonProperty("shares_for_free")
  private String sharesForFree;

  @JsonProperty("create_time")
  private String createTime;

  @JsonProperty("update_time")
  private String updateTime;

  @JsonProperty("rand_id")
  private String randId;

  @JsonProperty("is_owner")
  private String isOwner;

  private GroupVO group;
  private MemberVO member;

  @JsonProperty("has_paid")
  private int hasPaid;

  @JsonProperty("pay_tips")
  private String payTips;

  @JsonProperty("has_liked")
  private int hasLiked;

  private List<String> admires;
  private List<CommentVO> comments;
  private List<LikesVO> likes;

  @JsonProperty("user_need_shares")
  private int userNeedShares;

  @JsonProperty("is_collect")
  private int isCollect;

  @JsonProperty("content_visitor")
  private String contentVisitor;
}
