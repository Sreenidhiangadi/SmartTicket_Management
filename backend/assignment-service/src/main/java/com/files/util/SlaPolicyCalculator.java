package com.files.util;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class SlaPolicyCalculator {

    private SlaPolicyCalculator() {}

    public static Instant calculateDueAt(String priority) {

        return switch (priority) {
            case "CRITICAL" -> Instant.now().plus(1, ChronoUnit.HOURS);
            case "HIGH"     -> Instant.now().plus(2, ChronoUnit.HOURS);
            case "MEDIUM"   -> Instant.now().plus(4, ChronoUnit.HOURS);
            case "LOW"      -> Instant.now().plus(8, ChronoUnit.HOURS);
            default         -> Instant.now().plus(4, ChronoUnit.HOURS);
        };
    }
}
