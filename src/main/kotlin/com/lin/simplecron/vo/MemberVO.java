package com.lin.simplecron.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * jiemofetch.domains
 *
 * @author quanlinlin
 * @date 2022/9/13 20:23
 * @since
 */
@Data
public class MemberVO {

  private String id;
  private String uid;
  private String nickname;
  private String signature;
  private String position;

  @JsonProperty("headimgurl")
  private String headImgUrl;

  @JsonProperty("id_number")
  private String idNumber;
}
