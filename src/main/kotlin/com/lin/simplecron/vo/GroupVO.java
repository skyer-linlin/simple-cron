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
public class GroupVO {

  private String id;
  private String title;
  private String status;

  @JsonProperty("group_type")
  private String groupType;

  @JsonProperty("rand_id")
  private String randId;

  @JsonProperty("logo_url")
  private String logoUrl;
}
