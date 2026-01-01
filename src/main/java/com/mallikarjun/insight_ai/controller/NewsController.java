package com.mallikarjun.insight_ai.controller;

import com.mallikarjun.insight_ai.service.NewsAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/ai/news")
public class NewsController {
    private final ChatClient analystClient;
    private final ChatClient analystShort;
    private final ChatClient newsConsolidationClient;
    private final NewsAnalysisService newsAnalysisService;

    public NewsController(@Qualifier("analystClient") ChatClient analystClient, @Qualifier("analystShort") ChatClient analystShort, @Qualifier("newsConsolidationClient") ChatClient newsConsolidationClient, NewsAnalysisService newsAnalysisService) {
        this.analystClient = analystClient;
        this.analystShort = analystShort;
        this.newsConsolidationClient = newsConsolidationClient;
        this.newsAnalysisService = newsAnalysisService;
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
        return newsAnalysisService.analyzeNews(ticker);
    }
    @PostMapping("/news/all")
    public Map<String, String> analyzeNews(@RequestBody List<String> tickers) {

        return tickers.parallelStream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        ticker -> {
                            try {
                                newsAnalysisService.analyzeNews(ticker);
                                return "SUCCESS";
                            } catch (Exception e) {
                                return "FAILED";
                            }
                        }
                ));
    }

//    @GetMapping("/news")
//    public Object analyzeNews(@RequestParam String ticker) {
//        log.info("Analyzing news text: {}", ticker);
//
//        return newsConsolidationClient.prompt()
//                .user("Consolidate all news for " + ticker + " from today.")
//                .call()
//                .entity(Object.class);
//
////        return analystClient.prompt()
////                .system(systemMessage)
////                .user("News: " + newsText)
////                .call()
////                .entity(Object.class);
//    }
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
