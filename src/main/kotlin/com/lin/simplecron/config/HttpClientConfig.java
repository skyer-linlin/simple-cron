package com.lin.simplecron.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * com.lin.simplecron.config
 *
 * @author quanlinlin
 * @date 2022/9/17 01:36
 * @since
 */
@Configuration
public class HttpClientConfig {
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofMillis(3000))
            .setReadTimeout(Duration.ofMillis(3000))
            .build();
    }
}
