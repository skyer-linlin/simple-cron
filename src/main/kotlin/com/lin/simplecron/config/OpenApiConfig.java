package com.lin.simplecron.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * com.lin.simplecron.config
 *
 * @author quanlinlin
 * @date 2022/9/13 23:31
 * @since
 */
@Configuration
public class OpenApiConfig {
  @Bean
  public OpenAPI initOpenAPI() {
    return new OpenAPI()
        .info(new Info().title("My Simple Cron Task Project").description("OpenAPI").version("v1.0"));
  }
}
