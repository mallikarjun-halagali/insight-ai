package com.mallikarjun.insight_ai.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.mallikarjun.insight_ai.model.AiModelResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsAnalysisService {

    private final Cache<String, AiModelResponse> newsCache;
    private final ChatClient newsConsolidationClient;

    public AiModelResponse analyzeNews(String ticker) {

        String cacheKey = "news:analysis:" + ticker;

        return newsCache.get(cacheKey, key -> {
            log.info("Cache MISS for {}, calling LLM", ticker);

            return newsConsolidationClient.prompt()
                    .user("Consolidate all news for " + ticker + " from 24 Hours.")
                    .call()
                    .entity(AiModelResponse.class);
        });
    }
}
