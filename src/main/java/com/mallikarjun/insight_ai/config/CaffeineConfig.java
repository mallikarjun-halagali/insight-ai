package com.mallikarjun.insight_ai.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.mallikarjun.insight_ai.model.AiModelResponse;
import com.mallikarjun.insight_ai.model.Market;
import com.mallikarjun.insight_ai.util.CacheTtlCalculator;
import com.mallikarjun.insight_ai.util.MarketResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaffeineConfig {

    private final MarketResolver marketResolver;
    private final CacheTtlCalculator ttlCalculator;

    public CaffeineConfig(MarketResolver marketResolver,
                          CacheTtlCalculator ttlCalculator) {
        this.marketResolver = marketResolver;
        this.ttlCalculator = ttlCalculator;
    }

    @Bean
    public Cache<String, AiModelResponse> newsCache() {

        return Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfter(new Expiry<String, Object>() {

                    @Override
                    public long expireAfterCreate(
                            String key, Object value, long currentTime) {

                        String ticker = extractTicker(key);
                        Market market = marketResolver.resolveMarket(ticker);

                        return ttlCalculator
                                .ttlUntilMidnight(market)
                                .toNanos();
                    }

                    @Override
                    public long expireAfterUpdate(
                            String key, Object value,
                            long currentTime, long currentDuration) {
                        // Re-run the TTL logic so the "Late Night" rule applies to updates too
                        return expireAfterCreate(key, value, currentTime);
                    }

                    @Override
                    public long expireAfterRead(
                            String key, Object value,
                            long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                })
                .build();
    }

    private String extractTicker(String cacheKey) {
        // news:analysis:TCS.NS → TCS.NS
        int lastColon = cacheKey.lastIndexOf(':');
        return lastColon != -1 ? cacheKey.substring(lastColon + 1) : cacheKey;
    }
}
