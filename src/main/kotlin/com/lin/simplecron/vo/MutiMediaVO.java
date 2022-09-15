package com.lin.simplecron.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * com.lin.simplecron.vo
 *
 * @author quanlinlin
 * @date 2022/9/14 20:56
 * @since
 */
@Data
public class MutiMediaVO {
  private String url;
  private int height;
  private int width;
  private int size;

  @JsonProperty("mime_type")
  private String mimeType;

  private String original;
}
