package com.mallikarjun.insight_ai.util;

import com.mallikarjun.insight_ai.model.Market;
import org.springframework.stereotype.Component;

@Component
public class MarketResolver {

    public Market resolveMarket(String ticker) {

        if (ticker == null || ticker.isBlank()) {
            throw new IllegalArgumentException("Ticker cannot be null or empty");
        }

        String normalizedTicker = ticker.toUpperCase();

        if (normalizedTicker.endsWith(".NS") || normalizedTicker.endsWith(".BSE")) {
            return Market.INDIA;
        }

        return Market.US;
    }
}
