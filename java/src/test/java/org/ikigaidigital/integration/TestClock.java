package org.ikigaidigital.integration;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TestClock extends Clock {
    private Instant instant;

    public void setDate(LocalDate date) {
        this.instant = date.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    @Override
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }

    @Override
    public Instant instant() {
        return instant;
    }
}
