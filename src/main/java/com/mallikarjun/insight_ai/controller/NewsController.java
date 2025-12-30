package com.mallikarjun.insight_ai.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/ai/news")
public class NewsController {
    private final ChatClient analystClient;
    private final ChatClient analystShort;
    private final ChatClient newsConsolidationClient;

    public NewsController(@Qualifier("analystClient") ChatClient analystClient, @Qualifier("analystShort") ChatClient analystShort, @Qualifier("newsConsolidationClient") ChatClient newsConsolidationClient) {
        this.analystClient = analystClient;
        this.analystShort = analystShort;
        this.newsConsolidationClient = newsConsolidationClient;
    }
    @GetMapping("/consolidated-news")
    public Object getConsolidatedNews(@RequestParam String ticker) {
        log.info("Generating consolidated news report for ticker: {}", ticker);

        return analystClient.prompt()
                .user("Give me a consolidated impact report for " + ticker + " based on today's news.")
                .call()
                .entity(Object.class);
    }

    @GetMapping("/news")
    public Object analyzeNews(@RequestParam String ticker) {
        log.info("Analyzing news text: {}", ticker);

        return newsConsolidationClient.prompt()
                .user("Consolidate all news for " + ticker + " from today.")
                .call()
                .entity(Object.class);

//        return analystClient.prompt()
//                .system(systemMessage)
//                .user("News: " + newsText)
//                .call()
//                .entity(Object.class);
    }
    @GetMapping("/short")
    public Object shortAnalyzeNews(@RequestParam String ticker) {
        log.info("Analyzing shortAnalyzeNews text: {}", ticker);

        String systemMessage = """
        You are an expert Wall Street Analyst.
        Analyze the following news text and decide if it is GOOD or BAD for the stock.
        Return your answer in this format:
        SENTIMENT: [Positive/Negative/Neutral]
        REASON: [Short explanation]
        """;

        return analystShort.prompt()
                .user("Consolidate all news for " + ticker + " from today.")
                .call()
                .entity(Object.class);
    }

}
