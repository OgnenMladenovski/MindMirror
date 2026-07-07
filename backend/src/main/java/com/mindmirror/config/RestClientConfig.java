package com.mindmirror.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    /** RestClient pre-configured to reach the FastAPI AI microservice. */
    @Bean
    public RestClient aiRestClient(MindMirrorProperties props) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(props.getAi().getTimeoutMs()));
        factory.setReadTimeout(Duration.ofMillis(props.getAi().getTimeoutMs()));
        return RestClient.builder()
                .baseUrl(props.getAi().getBaseUrl())
                .requestFactory(factory)
                .build();
    }
}
