package com.mallikarjun.insight_ai.model;

import java.time.ZoneId;

public enum Market {
    INDIA(ZoneId.of("Asia/Kolkata")),
    US(ZoneId.of("America/New_York"));

    private final ZoneId zoneId;

    Market(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }
}

