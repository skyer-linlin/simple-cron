package com.lin.simplecron.domain;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.List;

/**
 * jiemofetch.domains
 *
 * @author quanlinlin
 * @date 2022/9/13 20:20
 * @since
 */
@Data
@Accessors(chain = true)
@ToString
@Document("jiemo_topic")
@Schema(name = "芥末圈主题", description = "芥末圈主题定义")
public class Topic {
    @MongoId
    private Integer id;

    @Schema(description = "圈子 id", required = true, example = "41228")
    private Integer groupId;

    private Integer groupMemberId;

    private Integer userId;
    private String type;
    private String preface;
    private String content;

    private String richContent;

    private List<String> multimedia;

    private String money;
    private Integer status;

    private Integer commentCount;

    private Integer admireCount;

    private Integer likeCount;

    private Integer shareCount;

    private String sharesForFree;

    private String randId;

    private Integer isOwner;

    private Member member;

    private String payTips;

    private Integer hasLiked;

    private List<Comment> comments;

    private Integer userNeedShares;

    private Integer isCollect;

    private String contentVisitor;

    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = "GMT+8")
    private LocalDateTime updateTime;
}
