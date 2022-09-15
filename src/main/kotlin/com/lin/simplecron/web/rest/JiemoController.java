package com.lin.simplecron.web.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * com.lin.simplecron.controller
 *
 * @author quanlinlin
 * @date 2022/9/14 18:03
 * @since
 */
@Controller
public class JiemoController {
  @GetMapping("/jiemo")
  public String index() {
    return "index";
  }
}
