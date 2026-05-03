package com.afa.devicer.back.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    @Bean
    public WebClient webClient(final WebClient.Builder builder) {
        return builder.build();
    }
}
