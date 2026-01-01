package com.mallikarjun.insight_ai.util;

import com.mallikarjun.insight_ai.model.Market;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class CacheTtlCalculator {

    public Duration ttlUntilMidnight(Market market) {
        ZoneId zoneId = market.getZoneId();

        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime nextMidnight = now
                .toLocalDate()
                .plusDays(1)
                .atStartOfDay(zoneId);

        return Duration.between(now, nextMidnight);
    }
}

