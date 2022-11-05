package com.lin.simplecron.vo;

import lombok.Data;

/**
 * jiemofetch.domains
 *
 * @author quanlinlin
 * @date 2022/9/13 20:18
 * @since
 */
@Data
public class JiemoResponse {
  private int code;
  private String message;
  private DataVO data;
}
