//package com.mallikarjun.insight_ai.config;
//
//import org.springframework.boot.webclient.WebClientCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class OpenRouterWebClientConfig {
//
//    @Bean
//    public WebClientCustomizer openRouterHeadersCustomizer() {
//        return webClientBuilder ->
//                webClientBuilder.defaultHeaders(headers -> {
//                    headers.add("HTTP-Referer", "http://localhost:8080");
//                    headers.add("X-Title", "Insight-AI");
//                });
//    }
//}
//
