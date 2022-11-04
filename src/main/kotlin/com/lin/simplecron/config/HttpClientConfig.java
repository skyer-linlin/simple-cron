package com.lin.simplecron.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
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
    @Bean("NoProxyRestTemplate")
    public RestTemplate getRestTemplate() {
        return new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofMillis(3000))
            .setReadTimeout(Duration.ofMillis(3000))
            .build();
    }

    @Bean(name = "ProxyRestTemplate")
    public RestTemplate getProxyRestTemplate() {
        SimpleClientHttpRequestFactory clientHttpReq = new SimpleClientHttpRequestFactory();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.31.32", 7890));
        clientHttpReq.setProxy(proxy);
        return new RestTemplate(clientHttpReq);
    }
}
