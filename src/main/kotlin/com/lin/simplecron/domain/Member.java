package com.lin.simplecron.domain;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * jiemofetch.domains
 *
 * @author quanlinlin
 * @date 2022/9/13 20:23
 * @since
 */
@Data
@ToString
@Accessors(chain = true)
public class Member {

  private Integer id;

  private Integer uid;
  private String nickname;
  private String signature;
  private Integer position;
  private String headImgUrl;
  private Integer idNumber;
}
