package com.mallikarjun.insight_ai.util;

import com.mallikarjun.insight_ai.model.Market;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class CacheTtlCalculator {

    // Define a cutoff hour (e.g., 20 = 8 PM).
    // News after 8 PM is considered "valid for tomorrow"
    private static final int CUTOFF_HOUR = 20;

    public Duration ttlUntilMidnight(Market market) {

        ZoneId zoneId = market.getZoneId();
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        ZonedDateTime targetMidnight;
        // EDGE CASE: If it's already late (e.g., 10 PM),
        // set expiry to Midnight of the NEXT day.
        if (now.getHour() >= CUTOFF_HOUR) {
            targetMidnight = now.toLocalDate()
                    .plusDays(2) // Skip today's midnight, go to tomorrow's
                    .atStartOfDay(zoneId);
        } else {
            targetMidnight = now.toLocalDate()
                    .plusDays(1)
                    .atStartOfDay(zoneId);
        }

        return Duration.between(now, targetMidnight);

    }
}
