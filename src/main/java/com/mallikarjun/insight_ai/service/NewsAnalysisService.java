package com.mallikarjun.insight_ai.service;

import com.mallikarjun.insight_ai.model.Market;
import com.mallikarjun.insight_ai.util.CacheTtlCalculator;
import com.mallikarjun.insight_ai.util.MarketResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsAnalysisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final MarketResolver marketResolver;
    private final CacheTtlCalculator ttlCalculator;
    private final ChatClient newsConsolidationClient;

    public Object analyzeNews(String ticker) {

        String cacheKey = "news:analysis:" + ticker;

        // 1️⃣ Cache hit
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("Cache HIT for {}", ticker);
            return cached;
        }

        // 2️⃣ Cache miss → expensive LLM call
        log.info("Cache MISS for {}, calling LLM", ticker);

        Object response = newsConsolidationClient.prompt()
                .user("Consolidate all news for " + ticker + " from today.")
                .call()
                .entity(Object.class);

        // 3️⃣ Calculate TTL
        Market market = marketResolver.resolveMarket(ticker);
        Duration ttl = ttlCalculator.ttlUntilMidnight(market);

        // 4️⃣ Store in cache
        redisTemplate.opsForValue()
                .set(cacheKey, response, ttl);

        return response;
    }
}
