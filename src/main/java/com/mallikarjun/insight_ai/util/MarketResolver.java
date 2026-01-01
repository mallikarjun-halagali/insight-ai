package com.mallikarjun.insight_ai.util;

import com.mallikarjun.insight_ai.model.Market;
import org.springframework.stereotype.Component;

@Component
public class MarketResolver {

    public Market resolveMarket(String ticker) {
        // simple rule – improve later using metadata
        if (ticker.endsWith(".NS") || ticker.endsWith(".BSE")) {
            return Market.INDIA;
        }
        return Market.US;
    }
}